@file:Suppress("UNCHECKED_CAST")

package com.common.tool.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.util.ArrayList


/**
 * @author 李雄厚
 *
 * @features 页面跳转
 */

inline fun <reified A> Context.openActivity(vararg pairs: Pair<String, Any>) {
    val bundle = Bundle(pairs.size).apply {
        for ((key, value) in pairs){
            when(value){
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Parcelable -> putParcelable(key, value)
                is ArrayList<*> -> putParcelableArrayList(key, value as ArrayList<out Parcelable>)
                is Float -> putFloat(key, value)
                is Double -> putDouble(key, value)
                is Boolean -> putBoolean(key, value)
            }
        }
    }

    val intent = Intent(this ,A::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}

inline fun <reified A> Context.openActivity() = startActivity(Intent(this ,A::class.java))



