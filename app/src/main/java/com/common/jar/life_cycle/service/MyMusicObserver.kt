package com.common.jar.life_cycle.service

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author 李雄厚
 *
 * @features ***
 */
class MyMusicObserver: LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        Log.e("测试", "onCreate Service")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStar(){
        Log.e("测试", "onStar Service")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(){
        Log.e("测试", "onDestroy Service")
    }
}