package com.common.tool.base

import androidx.lifecycle.ViewModel
import com.common.tool.event_live_bridge.EventLiveData

/**
 * @author 李雄厚
 *
 * @features ViewModel基类
 */
open class BaseViewModel: ViewModel() {
    val toastLiveData = EventLiveData<String>()

    val errorLiveData = EventLiveData<String>()
}