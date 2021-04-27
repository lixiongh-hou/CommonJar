package com.common.tool.bridge

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author 李雄厚
 *
 * @features 专为 Event LiveData 改造的底层 LiveData 支持
 */
abstract class ELiveData<T> {
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    var mDataLock = Any()

    companion object {
        const val START_VERSION = -1

        @SuppressWarnings("WeakerAccess") /* synthetic access */
        val NOT_SET = Any()

        fun assertMainThread(methodName: String) {
            if (!ArchTaskExecutor.getInstance().isMainThread) {
                throw IllegalStateException(
                    "Cannot invoke " + methodName + " on a background"
                            + " thread"
                )
            }
        }
    }

    private var mObservers: SafeIterableMap<EventObserver<T>, ObserverWrapper> = SafeIterableMap()

    // how many observers are in active state
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    var mActiveCount = 0

    @Volatile
    private var mData: Any? = null

    // when setData is called, we set the pending data and actual data swap happens on the main
    // thread
    @SuppressWarnings("WeakerAccess")
/* synthetic access */
    @Volatile
    var mPendingData: Any = NOT_SET
    private var mVersion = 0

    private var mDispatchingValue = false

    @SuppressWarnings("FieldCanBeLocal")
    private var mDispatchInvalidated: Boolean = false
    private val mPostValueRunnable = Runnable {
        var newValue: Any
        synchronized(mDataLock) {
            newValue = mPendingData
            mPendingData = NOT_SET
        }
        setEvent(newValue as Event<T>)
    }

    /**
     * Creates a LiveData initialized with the given `value`.
     *
     * @param value initial value
     */
    constructor(value: Event<T>) {
        mData = value
        mVersion = START_VERSION + 1
    }

    /**
     * Creates a LiveData with no value assigned to it.
     */
    constructor() {
        mData = NOT_SET
        mVersion = START_VERSION
    }

    @SuppressWarnings("unchecked")
    private fun considerNotify(observer: ObserverWrapper, data: T?) {
        if (!observer.mActive) {
            return
        }
        // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
        //
        // we still first check observer.active to keep it as the entrance for events. So even if
        // the observer moved to an active state, if we've not received that event, we better not
        // notify for a more predictable notification order.
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false)
            return
        }
        if (observer.mLastVersion >= mVersion) {
            return
        }
        observer.mLastVersion = mVersion
        if (data != null) {
            observer.mEventObserver(data)
        }
    }

    @SuppressWarnings("WeakerAccess")/* synthetic access */
    fun dispatchingValue(@Nullable initiator: ObserverWrapper?) {
        var initiator = initiator
        if (mDispatchingValue) {
            mDispatchInvalidated = true
            return
        }
        mDispatchingValue = true
        do {
            mDispatchInvalidated = false
            if (initiator != null) {
                considerNotify(initiator, null)
                initiator = null
            } else {
                val data = (mData as Event<T>).getContent() as T
                if (data != null) {
                    run {
                        val iterator: Iterator<Map.Entry<EventObserver<T>, ObserverWrapper>> =
                            mObservers.iteratorWithAdditions()
                        while (iterator.hasNext()) {
                            considerNotify(iterator.next().value, data)
                            if (mDispatchInvalidated) {
                                break
                            }
                        }
                    }
                }
            }
        } while (mDispatchInvalidated)
        mDispatchingValue = false
    }

    /**
     * Adds the given observer to the observers list within the lifespan of the given
     * owner. The events are dispatched on the main thread. If LiveData already has data
     * set, it will be delivered to the observer.
     * <p>
     * The observer will only receive events if the owner is in {@link Lifecycle.State#STARTED}
     * or {@link Lifecycle.State#RESUMED} state (active).
     * <p>
     * If the owner moves to the {@link Lifecycle.State#DESTROYED} state, the observer will
     * automatically be removed.
     * <p>
     * When data changes while the {@code owner} is not active, it will not receive any updates.
     * If it becomes active again, it will receive the last available data automatically.
     * <p>
     * LiveData keeps a strong reference to the observer and the owner as long as the
     * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
     * the observer &amp; the owner.
     * <p>
     * If the given owner is already in {@link Lifecycle.State#DESTROYED} state, LiveData
     * ignores the call.
     * <p>
     * If the given owner, observer tuple is already in the list, the call is ignored.
     * If the observer is already in the list with another owner, LiveData throws an
     * {@link IllegalArgumentException}.
     *
     * @param owner         The LifecycleOwner which controls the observer
     * @param eventObserver The observer that will receive the events
     */
    @MainThread
    fun observe(@NonNull owner: LifecycleOwner, @NonNull eventObserver: EventObserver<T>) {
        assertMainThread("observe")
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            // ignore
            return
        }
        val wrapper = LifecycleBoundObserver(owner, eventObserver)
        val existing: ObserverWrapper? = mObservers.putIfAbsent(eventObserver, wrapper)
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw IllegalArgumentException(
                "Cannot add the same observer"
                        + " with different lifecycles"
            )
        }
        if (existing != null) {
            return
        }
        owner.lifecycle.addObserver(wrapper)
    }

    /**
     * Adds the given observer to the observers list. This call is similar to
     * {@link ELiveData#observe(LifecycleOwner, EventObserver)} with a LifecycleOwner, which
     * is always active. This means that the given observer will receive all events and will never
     * be automatically removed. You should manually call {@link #removeObserver(EventObserver)} to stop
     * observing this LiveData.
     * While LiveData has one of such observers, it will be considered
     * as active.
     * <p>
     * If the observer was already added with an owner to this LiveData, LiveData throws an
     * {@link IllegalArgumentException}.
     *
     * @param eventObserver The observer that will receive the events
     */
    @MainThread
    fun observeForever(@NonNull eventObserver: EventObserver<T>) {
        assertMainThread("observeForever")
        val wrapper: AlwaysActiveObserver = AlwaysActiveObserver(eventObserver)
        val existing: ObserverWrapper? = mObservers.putIfAbsent(eventObserver, wrapper)
        require(existing !is LifecycleBoundObserver) {
            ("Cannot add the same observer"
                    + " with different lifecycles")
        }
        if (existing != null) {
            return
        }
        wrapper.activeStateChanged(true)
    }

    /**
     * Removes the given observer from the observers list.
     *
     * @param eventObserver The Observer to receive events.
     */
    @MainThread
    open fun removeObserver(eventObserver: EventObserver<T>) {
        assertMainThread("removeObserver")
        val removed: ObserverWrapper = mObservers.remove(eventObserver) ?: return
        removed.detachObserver()
        removed.activeStateChanged(false)
    }

    /**
     * Removes all observers that are tied to the given {@link LifecycleOwner}.
     *
     * @param owner The {@code LifecycleOwner} scope for the observers to be removed.
     */
    @MainThread
    open fun removeObservers(@NonNull owner: LifecycleOwner) {
        assertMainThread("removeObservers")
        for (entry: Map.Entry<EventObserver<T>, ObserverWrapper> in mObservers) {
            if (entry.value.isAttachedTo(owner)) {
                removeObserver(entry.key)
            }
        }
    }

    /**
     * Posts a task to a main thread to set the given value. So if you have a following code
     * executed in the main thread:
     * <pre class="prettyprint">
     * liveData.postValue("a");
     * liveData.setValue("b");
     * </pre>
     * The value "b" would be set at first and later the main thread would override it with
     * the value "a".
     * <p>
     * If you called this method multiple times before a main thread executed a posted task, only
     * the last value would be dispatched.
     *
     * @param value The new value
     */
    protected fun postEvent(value: Event<T>) {
        var postTask: Boolean
        synchronized(mDataLock) {
            postTask = mPendingData == NOT_SET
            mPendingData = value
        }
        if (!postTask) {
            return
        }
        ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable)
    }

    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     * <p>
     * This method must be called from the main thread. If you need set a value from a background
     * thread, you can use {@link #postValue(Object)}
     *
     * @param value The new value
     */
    @MainThread
    protected fun setEvent(value: Event<T>) {
        assertMainThread("setValue")
        mVersion++
        mData = value
        dispatchingValue(null)
    }

    /**
     * Returns the current value.
     * Note that calling this method on a background thread does not guarantee that the latest
     * value set will be received.
     *
     * @return the current value
     */
    @SuppressWarnings("unchecked")
    @Nullable
    open fun getValue(): Event<T>? {
        val data = mData!!
        return if (data !== NOT_SET) {
            data as Event<T>
        } else null
    }

    open fun getVersion(): Int = mVersion

    /**
     * Called when the number of active observers change to 1 from 0.
     *
     *
     * This callback can be used to know that this LiveData is being used thus should be kept
     * up to date.
     */
    open fun onActive() {}

    /**
     * Called when the number of active observers change from 1 to 0.
     *
     *
     * This does not mean that there are no observers left, there may still be observers but their
     * lifecycle states aren't [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED]
     * (like an Activity in the back stack).
     *
     *
     * You can check if there are observers via [.hasObservers].
     */
    open fun onInactive() {}

    /**
     * Returns true if this LiveData has observers.
     *
     * @return true if this LiveData has observers
     */
    @SuppressWarnings("WeakerAccess")
    open fun hasObservers(): Boolean = mObservers.size() > 0

    /**
     * Returns true if this LiveData has active observers.
     *
     * @return true if this LiveData has active observers
     */
    @SuppressWarnings("WeakerAccess")
    open fun hasActiveObservers(): Boolean = mActiveCount > 0

    inner class LifecycleBoundObserver(
        @NonNull owner: LifecycleOwner,
        eventObserver: EventObserver<T>
    ) :
        ObserverWrapper(eventObserver), LifecycleEventObserver {
        @NonNull
        val mOwner = owner

        override fun shouldBeActive(): Boolean =
            mOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (mOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                removeObserver(mEventObserver)
                return
            }
            activeStateChanged(shouldBeActive())
        }

        override fun isAttachedTo(owner: LifecycleOwner): Boolean = mOwner === owner

        override fun detachObserver() {
            mOwner.lifecycle.removeObserver(this)
        }
    }


    abstract inner class ObserverWrapper(eventObserver: EventObserver<T>) {
        var mEventObserver = eventObserver
        var mActive: Boolean = false
        var mLastVersion: Int = START_VERSION

        abstract fun shouldBeActive(): Boolean

        open fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return false
        }

        open fun detachObserver() {}

        fun activeStateChanged(newActive: Boolean) {
            if (newActive == mActive) {
                return
            }
            // immediately set active state, so we'd never dispatch anything to inactive
            // owner
            mActive = newActive
            val wasInactive = mActiveCount == 0
            mActiveCount += if (mActive) 1 else -1
            if (wasInactive && mActive) {
                onActive()
            }
            if (mActiveCount == 0 && !mActive) {
                onInactive()
            }
            if (mActive) {
                dispatchingValue(this)
            }
        }
    }

    inner class AlwaysActiveObserver(eventObserver: EventObserver<T>) : ObserverWrapper(eventObserver){

        override fun shouldBeActive(): Boolean = true

    }

}