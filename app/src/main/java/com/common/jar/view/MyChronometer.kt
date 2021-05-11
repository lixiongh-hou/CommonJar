package com.common.jar.view

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.Chronometer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author 李雄厚
 *
 * @features ***
 */
class MyChronometer(context: Context?, attrs: AttributeSet?) : Chronometer(context, attrs),
    LifecycleObserver {

    private var elapseTime = 0L


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun starMeter(){
        base = SystemClock.elapsedRealtime() - elapseTime
        start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun stopMeter(){
        elapseTime = SystemClock.elapsedRealtime() - base
        stop()
    }

}