package com.common.tool.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features 网络访问状态
 */
object NetworkUtil {

    /**
     * 判断网络是否可用
     * return  true可用  false 不可用
     */
    fun isNetworkConnected():Boolean{
        val connectivityManager = BaseApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isGrand = ContextCompat.checkSelfPermission(BaseApp.instance, Manifest.permission.ACCESS_NETWORK_STATE)== PackageManager.PERMISSION_GRANTED
        if (isGrand){
            val networkInfo = connectivityManager.activeNetworkInfo?:return false
            return networkInfo.isAvailable && networkInfo.isConnected
        }
        return isGrand
    }
}