package com.common.tool.base

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author 李雄厚
 *
 * @features Application基类
 */
open class BaseApp : Application(), ViewModelStoreOwner {

    private val factory: ViewModelProvider.Factory by lazy {
        return@lazy ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }
    private var appViewModelStore: ViewModelStore? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        appViewModelStore = ViewModelStore()
    }

    companion object {
        lateinit var instance: Application private set
    }

    fun appViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(activity.applicationContext as BaseApp, factory)
    }

    fun appViewModelProvider(activity: Application): ViewModelProvider {
        return ViewModelProvider(activity.applicationContext as BaseApp, factory)
    }

    override fun getViewModelStore(): ViewModelStore {
        if (appViewModelStore == null) {
            appViewModelStore = ViewModelStore()
        }
        return appViewModelStore as ViewModelStore
    }

}