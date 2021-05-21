package com.common.tool.notify

import androidx.lifecycle.ViewModel
import com.common.tool.event_live_bridge.EventLiveData

/**
 * @author 李雄厚
 *
 * @features 提醒模型
 */
class ReminderModel : ViewModel() {
    val reminder = EventLiveData<Reminder>()

}