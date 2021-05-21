package com.common.jar.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.common.jar.R
import com.common.jar.databinding.DialogMusicListBinding
import com.common.jar.databinding.ItemMusicListBinding
import com.common.jar.play_music.data.MusicList
import com.common.tool.base.BaseFragmentDialog
import com.common.tool.base.rv.BaseAdapter
import com.common.tool.util.ScreenUtil.screenHeight
import com.common.tool.util.dpToPx
import com.common.tool.util.pxToDp

/**
 * @author 李雄厚
 *
 * @features 音乐列表对话框MusicListDialog
 */
class MusicListDialog : BaseFragmentDialog<DialogMusicListBinding>() {
    private var musicList: ArrayList<MusicList>? = null
    private var position = -1
    var playClickEvent: ((Int) -> Unit)? = null

    companion object {
        fun instance(list: ArrayList<MusicList>, position: Int) = MusicListDialog().apply {
            setAnimStyle(R.style.DialogBottomAnim)
            setGravity(Gravity.BOTTOM)
            setSize(height = (screenHeight - 150.dpToPx).pxToDp)
            arguments = Bundle().apply {
                putParcelableArrayList("list", list)
                putInt("position", position)
            }
        }
    }

    override fun convertView(binding: DialogMusicListBinding) {
        arguments?.let {
            musicList = it.getParcelableArrayList("list")
            position = it.getInt("position", -1)
        }
        val adapter = MusicListAdapter()
        binding.adapter = adapter
        musicList?.let {
            adapter.addAllData(it)
        }
        adapter.clickEvent = { _, _, position ->
            playClickEvent?.invoke(position)
            dismiss()
        }
    }

    inner class MusicListAdapter : BaseAdapter<MusicList, ItemMusicListBinding>() {
        override fun createBinding(parent: ViewGroup, viewType: Int): ItemMusicListBinding =
            ItemMusicListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        override fun bind(binding: ItemMusicListBinding, data: MusicList, position: Int) {
            binding.data = data
            binding.select = position == this@MusicListDialog.position
        }
    }
}