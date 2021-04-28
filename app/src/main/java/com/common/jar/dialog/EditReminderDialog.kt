package com.common.jar.dialog

import android.os.Bundle
import com.common.jar.databinding.DialogEditBinding
import com.common.jar.reminder.AddReminderFragment.Companion.pickerHourList
import com.common.jar.reminder.AddReminderFragment.Companion.pickerMinuteList
import com.common.jar.reminder.ReminderAdapter.Companion.setTimeLong
import com.common.tool.R
import com.common.tool.base.BaseFragmentDialog
import com.common.tool.notify.Reminder
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.NonDoubleClick.clickWithTrigger

/**
 * @author 李雄厚
 *
 * @features 修改提醒时间
 */
class EditReminderDialog : BaseFragmentDialog<DialogEditBinding>() {
    private var hourValue = 0
    private var houString = ""
    private var minuteValue = 0
    private var minuteString = ""
    var clickEvent : ((Reminder) -> Unit)? = null
    var moreSettingsEvent : ((Reminder) -> Unit)? = null
    companion object {
        fun instance(reminder: Reminder): EditReminderDialog =
            EditReminderDialog().apply {
                setAnimStyle(R.style.DialogCentreAnim)
                setMargin(10)
                arguments = Bundle().apply {
                    putParcelable("reminder", reminder)
                }
            }
    }

    override fun convertView(binding: DialogEditBinding) {
        val reminder: Reminder? = arguments?.getParcelable("reminder")
        binding.data = reminder
        binding.remainder = "${DateTimeUtil.formatTime(setTimeLong(reminder?.time?:"00:00"))}后响铃"
        binding.turnNnView.setOnClickListener {
            reminder?.turnNn = !reminder!!.turnNn
            binding.data = reminder
        }
        initPickView(binding, reminder)

        binding.complete.clickWithTrigger {
            clickEvent?.invoke(reminder!!)
            dismiss()
        }
        binding.moreSettings.clickWithTrigger {
            moreSettingsEvent?.invoke(reminder!!)
            dismiss()
        }
    }


    private fun initPickView(binding: DialogEditBinding,  reminder: Reminder?) {
        val timeList = (reminder?.time?:"00:00").split(":")
        for (i in pickerHourList.indices) {
            if (pickerHourList[i] == timeList[0]) {
                hourValue = i
                houString = pickerHourList[i]
            }
        }
        for (i in pickerMinuteList.indices) {
            if (pickerMinuteList[i] == timeList[1]) {
                minuteValue = i
                minuteString = pickerMinuteList[i]
            }
        }

        binding.pickerHour.refreshByNewDisplayedValues(pickerHourList.toTypedArray())
        binding.pickerHour.value = hourValue
        binding.pickerHour.setOnValueChangedListener { _, _, newVal ->
            houString = newVal.toString()
            binding.remainder =
                "${DateTimeUtil.formatTime(setTimeLong("$houString:$minuteString"))}后响铃"
            reminder?.time = "$houString:$minuteString"
            binding.data = reminder
        }

        binding.pickerMinute.refreshByNewDisplayedValues(pickerMinuteList.toTypedArray())
        binding.pickerMinute.value = minuteValue
        binding.pickerMinute.setOnValueChangedListener { _, _, newVal ->
            minuteString = newVal.toString()
            binding.remainder =
                "${DateTimeUtil.formatTime(setTimeLong("$houString:$minuteString"))}后响铃"
            reminder?.time = "$houString:$minuteString"
            binding.data = reminder
        }
    }
}