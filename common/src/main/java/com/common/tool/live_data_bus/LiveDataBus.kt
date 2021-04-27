package com.common.tool.live_data_bus

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by 袁从斌 on 2020/6/8.
 */
class LiveDataBus {
    companion object {
        val liveDataBus by lazy {
            LiveDataBus()
        }
    }

    val hashMap = ConcurrentHashMap<String, StickyLiveData<*>>()

    fun <T> with(eventName: String): StickyLiveData<T> {
        var liveData = hashMap[eventName]
        if (liveData == null) {
            liveData = StickyLiveData<T>(eventName)
            hashMap[eventName] = liveData
        }
        return liveData as StickyLiveData<T>
    }

    inner class StickyLiveData<T>(private var eventName: String) : LiveData<T>() {
        var stickyData: T? = null
            set(stickyData) {
                field = stickyData
                field?.let { setValue(it) }
            }

        var version = 0
            private set

        public override fun setValue(value: T) {
           version++
           super.setValue(value)
       }

       public override fun postValue(value: T) {
           version++
           super.postValue(value)
       }
        fun postStickyData(value: T?) {
            this.stickyData = value
            this.stickyData?.let {
                postValue(it)
            }
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner, observer)
        }

        fun observerSticky(owner: LifecycleOwner, observer: Observer<in T>, sticky: Boolean = false) {
            super.observe(owner, WrapperObserver<T>(this, observer, sticky))
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        hashMap.remove(eventName)
                    }
                }
            })
        }
    }

    class WrapperObserver<T>(private var liveData: StickyLiveData<T>,
                             private var observer: Observer<in T>,
                             private var sticky: Boolean = false) : Observer<T> {

        //标记该liveData已经发射几次数据了,过滤老数据重复接收
        private var lastVersion: Int = 0

        init {
            //比如先使用StickyLiveData发送了一条数据,StickyLiveData#version=1
            //那么当我们创建WrapperObserver注册进去的时候,就至少需要把他的version和StickyLiveData的version保持一致
            //用于过滤老数据
            lastVersion = liveData.version
        }

        override fun onChanged(t: T) {
            //如果当前observer收到数据的次数已经大于等于StickyLiveData发送数据的个数了,则return

            /**
             * observer.lastVersion>=liveData.version
             * 这种情况只会出现在我们先行创建一个liveData发射了一条数据.此时liveDate的version=1.
             *
             * 而后注册一个observer进去,由于我们代理了传递进来的observer,进入包装成wrapperObserver,此时wrapperObserver的lastVersion就会根liveData的version对齐,保持一致.
             * 把wrapperObserver注册到liveData中.
             *
             * 根据liveData的原理,一旦一个新的observer注册进去,也是会尝试把数据派发给他.这就是黏性事件(先发送,后接收)
             *
             * 但此时wrapperObserver的lastVersion已经和liveData的version一样了.由此来控制黏性事件的分发与否
             *
             */
            if (lastVersion>=liveData.version){
                //如果当前的observer他是黏性事件的,测发送过去
                if (sticky && liveData.stickyData!=null){
                    observer.onChanged(liveData.stickyData)
                }
                return
            }
            lastVersion = liveData.version
            observer.onChanged(t)
        }
    }
}