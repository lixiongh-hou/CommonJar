package com.common.tool.dialog

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.common.tool.R
import com.common.tool.base.BaseFragmentDialog
import com.common.tool.databinding.DialogReminderBinding
import com.common.tool.notify.Reminder
import com.common.tool.notify.ReminderNotifyManager
import com.common.tool.sp.SharedPreferenceUtils
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.SpanBuilder

/**
 * @author 李雄厚
 *
 * @features 提醒对话框
 */
class ReminderDialog : BaseFragmentDialog<DialogReminderBinding>() {

    companion object {
        private val dialog: ReminderDialog by lazy {
            ReminderDialog()
        }

        fun show(fragmentManager: FragmentManager, reminder: Reminder) {
            val string = SpanBuilder().apply {
                append("已到")
                append(reminder.remarks)
                setForegroundColor(Color.RED)
                append("检测时间段\n")
            }.create()
            if (reminder.rule == "只响一次") {
                val reminders =  SharedPreferenceUtils.getListData("reminder", Reminder::class.java)
                for (i in reminders){
                    if (i.reminderId == reminder.reminderId){
                        i.turnNn = false
                    }
                }
                SharedPreferenceUtils.putListData("reminder", reminders)
            }

            if (dialog.isAdded || dialog.isResumed || dialog.isVisible) {
                dialog.binding.tvContent.append(string)
            } else {
                with(dialog) {
                    setOutCancel(false)
                    setAnimStyle(R.style.DialogCentreAnim)
                    setMargin(50)
                    arguments = Bundle().apply {
                        putCharSequence("content", string)
                    }
                }
                dialog.show(fragmentManager, "reminder")
            }
            if (reminder.rule != "只响一次"){
                reminder.timeLong = setTimeLong(reminder.time)
                ReminderNotifyManager.setNotify(reminder)
                reminder.timeLong = -1L

            }
        }

        private  fun setTimeLong(hhAndMm: String): Long {
            val time = "${DateTimeUtil.currentTime(DateTimeUtil.YYYY_MM)}-${(DateTimeUtil.currentTime(DateTimeUtil.DD).toInt() + 1)} ${hhAndMm}:00"
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


    override fun convertView(binding: DialogReminderBinding) {
        val string = arguments?.getCharSequence("content") ?: ""
        binding.tvContent.text = string
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }
}