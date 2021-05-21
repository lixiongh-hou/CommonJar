package com.common.jar.play_music

import android.view.LayoutInflater
import android.view.ViewGroup
import com.common.jar.databinding.ItemPlayMusicListBinding
import com.common.jar.play_music.data.MusicList
import com.common.tool.base.rv.BaseAdapter

/**
 * @author ¿Ó–€∫Ò
 *
 * @features “Ù¿÷¡–±Ì  ≈‰∆˜
 */
class PlayMusicListAdapter : BaseAdapter<MusicList, ItemPlayMusicListBinding>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemPlayMusicListBinding =
        ItemPlayMusicListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bind(binding: ItemPlayMusicListBinding, data: MusicList, position: Int) {
        binding.data = data
    }
}