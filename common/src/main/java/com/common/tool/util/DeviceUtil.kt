package com.common.tool.util

import android.app.ActivityManager
import android.content.Context
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features 手机相关工具类
 */
object DeviceUtil {

    fun isServiceRunning(className: String): Boolean{
        val activityManager = BaseApp.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (service in runningServices) {
            if (service.service.className == className) {
                return true
            }
        }
        return false
    }
}