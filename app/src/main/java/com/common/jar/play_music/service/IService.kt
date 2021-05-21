package com.common.jar.play_music.service

import com.common.jar.play_music.data.MusicList

/**
 * @author 李雄厚
 *
 * @features ***
 */
interface IService {
    /**
     * 更新播放状态
     */
    fun updatePlatState()

    /**
     * 获取当前播放状态
     */
    fun isPlaying(): Boolean?

    /**
     * 获取总进度
     */
    fun getDuration(): Int

    /**
     * 获取当前进度
     */
    fun getProgress(): Int

    /**
     * 更新播放进度
     */
    fun seekTo(progress: Int)

    /**
     * 更新播放模式
     */
    fun updatePlayMode()

    /**
     * 获取播放模式
     */
    fun getPlayMode(): Int

    /**
     * 播放下一首
     */
    fun playNext()

    /**
     * 播放上一首
     */
    fun playPre()

    /**
     * 获取当前音乐列表
     */
    fun getMusicList(): ArrayList<MusicList>?

    /**
     * 获取当前播放位置
     */
    fun getPosition(): Int
    /**
     * 设置当前播放位置
     */
    fun setPosition(position: Int)
}