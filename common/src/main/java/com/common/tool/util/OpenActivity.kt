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
    val bundle = Bundle()
    pairs.forEach {
        when(it.second){
            is String -> bundle.putString(it.first, it.second as String)
            is Int -> bundle.putInt(it.first, it.second as Int)
            is Parcelable -> bundle.putParcelable(it.first, it.second as Parcelable)
            is ArrayList<*> -> {
                if (it.second is ArrayList<*>) {
                    bundle.putParcelableArrayList(it.first, it.second as ArrayList<out Parcelable>)
                }
            }
            is Float -> bundle.putFloat(it.first, it.second as Float)
            is Double -> bundle.putDouble(it.first, it.second as Double)
            is Boolean -> bundle.putBoolean(it.first, it.second as Boolean)
        }
    }
    val intent = Intent(this ,A::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}

inline fun <reified A> Context.openActivity() = startActivity(Intent(this ,A::class.java))


