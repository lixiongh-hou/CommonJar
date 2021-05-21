package com.common.jar.play_music.model

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.common.jar.play_music.config.LocalConfig
import com.common.jar.play_music.data.MusicList
import com.common.tool.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @author ¿Ó–€∫Ò
 *
 * @features ***
 */
class PlayMusicListModel : BaseViewModel() {
    val musicList = MutableLiveData<MutableList<MusicList>>()

    fun queryMusicList(context: Context) {
        viewModelScope.launch {
            LocalConfig.queryMusicList(context) {
                musicList.postValue(it)
            }
        }
    }
}