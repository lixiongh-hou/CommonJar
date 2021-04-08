package com.common.tool.base

import androidx.lifecycle.ViewModel
import com.common.tool.data.bridge.EventLiveData

/**
 * @author 李雄厚
 *
 * @features ViewModel基类
 */
open class BaseViewModel: ViewModel() {
    val toastLiveData = EventLiveData<String>()
}