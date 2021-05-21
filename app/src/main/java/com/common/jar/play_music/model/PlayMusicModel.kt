package com.common.jar.play_music.model

import androidx.databinding.ObservableField
import com.common.tool.base.BaseViewModel

/**
 * @author 李雄厚
 *
 * @features ***
 */
class PlayMusicModel: BaseViewModel() {

    var durationTime = ObservableField<String>()
    var progressTime = ObservableField<String>()
}