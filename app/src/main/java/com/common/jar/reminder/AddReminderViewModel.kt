package com.common.jar.reminder

import androidx.databinding.ObservableField
import com.common.tool.base.BaseViewModel
import com.common.tool.live_data_bus.LiveDataBus
import com.common.tool.notify.Reminder
import java.util.*

/**
 * @author 李雄厚
 *
 * @features ***
 */
class AddReminderViewModel : BaseViewModel() {

    val remarks = ObservableField<String>()
    val rule = ObservableField<String>("只响一次")


    fun addReminder(hhAndMm: String) {
        LiveDataBus.liveDataBus.with<Reminder>("addReminder").setValue(
            Reminder(
                UUID.randomUUID().toString(),
                hhAndMm,
                remarks = remarks.get() ?: "测试",
                rule = rule.get() ?: "只响一次",
                turnNn = true
            ).apply { timeLong = 10 * 1000 }
        )
    }
}