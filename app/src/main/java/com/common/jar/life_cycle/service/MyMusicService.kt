package com.common.jar.life_cycle.service

import androidx.lifecycle.LifecycleService

/**
 * @author 李雄厚
 *
 * @features ***
 */
class MyMusicService: LifecycleService() {

    init {
        lifecycle.addObserver(MyMusicObserver())
    }

}