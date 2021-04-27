package com.common.tool.notify

import android.util.Log
import androidx.work.*
import com.common.tool.base.BaseApp
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.DateTimeUtil.formatTime
import java.util.concurrent.TimeUnit

/**
 * @author 李雄厚
 *
 * @features 闹钟提醒通知
 */
object ReminderNotifyManager {

    var CURRENT_YEAR_MONTH_AND_DAY = DateTimeUtil.currentTime(DateTimeUtil.YYYY_MM_DD)
    private var worker = WorkManager.getInstance(BaseApp.instance)

    fun setNotify(reminder: Reminder) {
        if (reminder.timeLong < 0) {
            return
        }
        worker.cancelUniqueWork(reminder.reminderId)
        worker.enqueueUniqueWork(
            reminder.reminderId,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<MeasureReminderWorker>()
                .setInitialDelay(reminder.timeLong, TimeUnit.MILLISECONDS)
                .setInputData(getInputData(reminder))
                .build()
        )
        Log.e("测试", "${reminder.remarks}通知，${formatTime(reminder.timeLong, true)}后响铃")
    }

    fun cancelWork(uniqueWorkName: String){
        worker.cancelUniqueWork(uniqueWorkName)
    }


    private fun getInputData(reminder: Reminder): Data =
        Data.Builder()
            .putString("reminderId", reminder.reminderId)
            .putString("time", reminder.time)
            .putString("rule", reminder.rule)
            .putString("remarks", reminder.remarks)
            .putBoolean("turnNn", reminder.turnNn)
            .build()
}