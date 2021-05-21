package com.common.tool.event_live_bridge

/**
 * @author 李雄厚
 *
 * @features 专为 Event LiveData 改造的底层 LiveData 支持
 */
//interface EventObserver<T> {
//
//    fun onReceived(t: T)
//}

typealias EventObserver<T> = (t: T) -> Unit