package com.common.jar.play_music.service


/**
 * @author 李雄厚
 *
 * @features 播放模式
 */

object Mode {
    /**
     * 顺序播放
     */
    const val MODE_ALL = 1

    /**
     * 单曲循环
     */
    const val MODE_SINGLE = 2

    /**
     * 随机播放
     */
    const val MODE_RANDOM = 3


}

object States{
    /**
     * 上一首
     */
    const val FROM_PER = 1

    /**
     * 下一首
     */
    const val FROM_NEXT = 2

    /**
     * 播放暂停
     */
    const val FROM_STATES = 3

    /**
     * 主体
     */
    const val FROM_CONTENT = 4
}