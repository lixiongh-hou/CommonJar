package com.common.tool.event_live_bridge

/**
 * @author 李雄厚
 *
 * @features 用于 callback 的情况，配合 EventLiveData & SharedViewModel 的使用
 * 建议使用良好封装的 EventLiveData，而不要通过 MutableLiveData 套一个 Event 的方式，避免在 java 环境下造成 null 安全的一致性问题
 */
class Event<T>(private var content: T) {

    private var hasHandled = false

    fun getContent(): T? {
        if (hasHandled) {
            return null
        }
        hasHandled = true
        return content
    }
    fun setContent(content: T) {
        this.content = content
    }
}