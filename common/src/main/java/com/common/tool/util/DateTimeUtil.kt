package com.common.tool.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author 李雄厚
 *
 * @features 时间工具类
 */
object DateTimeUtil {

    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val HH_MM = "HH:mm"
    const val SS = "ss"
    const val MM_DD_HH_MM = "MM-dd HH:mm"
    const val HH_MM_SS = "HH:mm:ss"
    const val MM_DD = "MM-dd"


    fun currentTime(): String {
        val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }


    fun currentTime(format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    fun transform(date: String, originFormat: String, newFormat: String): String {
        if (date.isEmpty()) {
            return ""
        }
        val sdf = SimpleDateFormat(originFormat, Locale.getDefault())
        try {
            val d = sdf.parse(date) ?: return ""
            val time = d.time
            sdf.applyPattern(newFormat)
            return sdf.format(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun currentFormat(milliseconds: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(milliseconds))
    }

    fun getTimeMillis(date: String, format: String): Long {
        if (date.isEmpty()) {
            return 0
        }
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        try {
            val d = sdf.parse(date) ?: return 0
            return d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 根据时间戳得到具体还剩多长时间
     */
    fun formatTime(ms: Long, showSecond: Boolean = false): String {
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24

        //天
        val day = ms / dd
        //时
        val hour = (ms - day * dd) / hh
        //分
        val minute = (ms - day * dd - hour * hh) / mi
        //秒
        val second = (ms - day * dd - hour * hh - minute * mi) / ss
        //毫秒
        val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss

        val sb = StringBuilder()
        when {
            day > 0 -> sb.append(day).append("天")
            hour > 0 -> sb.append(hour).append("小时").append(minute).append("分钟")
            minute > 0 -> sb.append(minute).append("分钟")
            else -> if (showSecond) sb.append(second).append("秒") else sb.append("不到1分钟")
        }
        return sb.toString()
    }
}