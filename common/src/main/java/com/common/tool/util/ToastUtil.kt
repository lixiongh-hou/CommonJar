package com.common.tool.util

import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features 吐司方法
 */
object ToastUtil {
    private val sToast: Toast by lazy {
        return@lazy Toast.makeText(BaseApp.instance, "", Toast.LENGTH_SHORT)
    }

    @SuppressLint("RestrictedApi")
    @JvmStatic
    @JvmOverloads
    fun Any.toast(duration: Int = Toast.LENGTH_SHORT) {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            ArchTaskExecutor.getInstance().postToMainThread {
                sToast.duration = duration
                sToast.setText(this.toString())
                sToast.show()
            }
        } else {
            sToast.duration = duration
            sToast.setText(this.toString())
            sToast.show()
        }

    }
}