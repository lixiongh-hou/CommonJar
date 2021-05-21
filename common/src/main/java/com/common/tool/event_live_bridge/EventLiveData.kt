package com.common.tool.event_live_bridge

/**
 * @author 李雄厚
 *
 * @features 用于 callback 的情况，配合 MutableLiveData & SharedViewModel 的使用
 */
@SuppressWarnings("WeakerAccess")
class EventLiveData<T> : ELiveData<T> {
    /**
     * Creates a MutableLiveData initialized with the given {@code value}.
     *
     * @param value initial value
     */
    constructor(value: Event<T>) : super(value)

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    constructor():super()

    fun postValue(value: T) {
        super.postEvent(Event(value))
    }

    fun setValue(value: T) {
        super.setEvent(Event(value))
    }
}