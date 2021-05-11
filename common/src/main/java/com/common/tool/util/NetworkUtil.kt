package com.common.tool.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.common.tool.base.BaseApp
import com.common.tool.network_status.NetworkType


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
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            BaseApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isGrand = ContextCompat.checkSelfPermission(
            BaseApp.instance,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED
        if (isGrand) {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isAvailable && networkInfo.isConnected
        }
        return isGrand
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * 获取当前网络类型
     * 需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    fun getNetworkType(context: Context): NetworkType {
        var netType = NetworkType.NETWORK_NO
        val info = getActiveNetworkInfo(context)
        if (info != null && info.isAvailable) {
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    netType = NetworkType.NETWORK_WIFI
                }
                ConnectivityManager.TYPE_MOBILE -> {
                    when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA,
                        TelephonyManager.NETWORK_TYPE_EVDO_A,
                        TelephonyManager.NETWORK_TYPE_UMTS,
                        TelephonyManager.NETWORK_TYPE_EVDO_0,
                        TelephonyManager.NETWORK_TYPE_HSDPA,
                        TelephonyManager.NETWORK_TYPE_HSUPA,
                        TelephonyManager.NETWORK_TYPE_HSPA,
                        TelephonyManager.NETWORK_TYPE_EVDO_B,
                        TelephonyManager.NETWORK_TYPE_EHRPD,
                        TelephonyManager.NETWORK_TYPE_HSPAP -> netType = NetworkType.NETWORK_3G
                        TelephonyManager.NETWORK_TYPE_LTE,
                        TelephonyManager.NETWORK_TYPE_IWLAN -> netType = NetworkType.NETWORK_4G
                        TelephonyManager.NETWORK_TYPE_GSM,
                        TelephonyManager.NETWORK_TYPE_GPRS,
                        TelephonyManager.NETWORK_TYPE_CDMA,
                        TelephonyManager.NETWORK_TYPE_EDGE,
                        TelephonyManager.NETWORK_TYPE_1xRTT,
                        TelephonyManager.NETWORK_TYPE_IDEN -> netType = NetworkType.NETWORK_2G
                        else -> {
                            val subtypeName = info.subtypeName
                            netType = if (subtypeName.equals("TD-SCDMA", true) ||
                                subtypeName.equals("WCDMA", true) ||
                                subtypeName.equals("CDMA2000", true)
                            ) {
                                NetworkType.NETWORK_3G
                            }else{
                                NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                }
                else -> {
                    netType = NetworkType.NETWORK_UNKNOWN
                }
            }
        }
        return netType
    }
    
}