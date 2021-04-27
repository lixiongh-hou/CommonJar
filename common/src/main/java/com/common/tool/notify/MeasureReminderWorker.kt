package com.common.tool.notify

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features Worker通知
 */
class MeasureReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val handler = Handler(Looper.getMainLooper())
    override fun doWork(): Result {
        val data = inputData
        val reminderId = data.getString("reminderId") ?: ""
        val remarks = data.getString("remarks") ?: ""
        val time = data.getString("time") ?: ""
        val rule = data.getString("rule") ?: ""
        val turnNn = data.getBoolean("turnNn", false)
        Log.e("测试", "worker接收到数据,,reminderId,$reminderId,,remarks,$remarks,,time$time,,rule$rule,,turnNn$turnNn")
        handler.post {
            (BaseApp.instance as BaseApp).appViewModelProvider(BaseApp.instance)
                .get(ReminderModel::class.java)
                .reminder.postValue(Reminder(reminderId, time, rule = rule, remarks = remarks, turnNn = turnNn))
        }
//        val outputData = workDataOf("reminderId" to reminderId, "name" to name, "time" to time)
//        return Result.success(outputData)
        return Result.success()
    }
}