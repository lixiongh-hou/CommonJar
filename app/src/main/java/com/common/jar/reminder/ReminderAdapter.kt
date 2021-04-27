package com.common.jar.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.common.jar.databinding.ItemReminderBinding
import com.common.tool.base.rv.BaseAdapter
import com.common.tool.notify.Reminder
import com.common.tool.notify.ReminderNotifyManager
import com.common.tool.notify.ReminderNotifyManager.CURRENT_YEAR_MONTH_AND_DAY
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.DateTimeUtil.formatTime

/**
 * @author 李雄厚
 *
 * @features 提醒列表适配器
 */
class ReminderAdapter : BaseAdapter<Reminder, ItemReminderBinding>() {
    var checkedChangeListener: ((Boolean, String) -> Unit)? = null
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemReminderBinding =
        ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bind(binding: ItemReminderBinding, data: Reminder, position: Int) {
        binding.remainder = "${formatTime(setTimeLong(data.time))}后响铃"
        binding.selectedDel = data.selectedDel
        binding.selected = data.selected
        binding.data = data
        binding.turnNnView.setOnClickListener {
            data.turnNn = !data.turnNn
            binding.data = data
            if (data.turnNn) {
                data.timeLong = setTimeLong(data.time)
                ReminderNotifyManager.setNotify(data)
            } else {
                ReminderNotifyManager.cancelWork(data.reminderId)
            }
            checkedChangeListener?.invoke(data.turnNn, data.reminderId)
        }
    }

    companion object {
        fun setTimeLong(hhAndMm: String): Long {
            val time = "$CURRENT_YEAR_MONTH_AND_DAY ${hhAndMm}:00"
            return if (DateTimeUtil.getTimeMillis(
                    time,
                    DateTimeUtil.YYYY_MM_DD_HH_MM_SS
                ) > System.currentTimeMillis()
            ) {
                DateTimeUtil.getTimeMillis(
                    time,
                    DateTimeUtil.YYYY_MM_DD_HH_MM_SS
                ) - System.currentTimeMillis()
            } else {
                val temporaryLong = System.currentTimeMillis() - DateTimeUtil.getTimeMillis(
                    time,
                    DateTimeUtil.YYYY_MM_DD_HH_MM_SS
                )
                (24 * 60 * 60 * 1000) - temporaryLong
            }
        }

    }
}