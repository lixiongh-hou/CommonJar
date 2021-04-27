package com.common.jar

import com.common.tool.bridge.EventLiveData

/**
 * @author 李雄厚
 *
 * @features 修改标题通知
 */
object EditTitleLiveData {
    val updateLiveData = EventLiveData<String>()

    fun post(string: String){
        if (updateLiveData.hasObservers()){
            updateLiveData.postValue(string)
        }
    }

}