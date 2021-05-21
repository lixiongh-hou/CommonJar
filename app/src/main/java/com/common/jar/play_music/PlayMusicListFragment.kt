package com.common.jar.play_music

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.FragmentPlayMusicListBinding
import com.common.jar.play_music.data.MusicList
import com.common.jar.play_music.model.PlayMusicListModel
import com.common.tool.base.BaseFragment
import com.common.tool.base.rv.BaseAdapter
import com.common.tool.util.openActivity
import com.common.tool.util.permission.PermissionUtil
import com.ftd.livepermissions.LivePermissions
import com.ftd.livepermissions.PermissionResult
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_play_music_list.*

/**
 * @author 李雄厚
 *
 * @features 音乐列表
 */
class PlayMusicListFragment : BaseFragment<FragmentPlayMusicListBinding, PlayMusicListModel>() {
    private lateinit var adapter: BaseAdapter<MusicList, *>

    override fun onBackClickListener() {
        requireActivity().finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        initTitleBar("音乐列表")
        getTitleBar().setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )
        getTitleBar().findViewById<AppCompatTextView>(R.id.baseTitleText).run {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleClose).run {
            imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        adapter = PlayMusicListAdapter()
        binding.adapter = adapter

        LivePermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).observe(this, Observer {
            when (it) {
                is PermissionResult.Grant -> {  //权限允许
                    Log.e("测试", "权限允许")
                    model.queryMusicList(requireContext())
                }
                is PermissionResult.Rationale -> {  //权限拒绝
                    it.permissions.forEach { s ->
                        println("Rationale:${s}")//被拒绝的权限
                    }
                    Log.e("测试", "权限拒绝")
                }
                is PermissionResult.Deny -> {   //权限拒绝，且勾选了不再询问
                    it.permissions.forEach { s ->
                        println("deny:${s}")//被拒绝的权限
                    }
                    Log.e("测试", "权限拒绝，且勾选了不再询问")
                }
            }
        })
    }

    private fun navigationPlayMusicFragment(position: Int) {
        requireContext().openActivity<PlayMusicActivity>(
            "list" to adapter.data,
            "position" to position
        )
    }

    override fun initData() {
        model.musicList.observe(viewLifecycleOwner, Observer {
            Log.e("测试", Gson().toJson(it))
            adapter.addAllData(it)
        })

        adapter.clickEvent = { _, _, position ->
            navigationPlayMusicFragment(position)
        }
    }
}