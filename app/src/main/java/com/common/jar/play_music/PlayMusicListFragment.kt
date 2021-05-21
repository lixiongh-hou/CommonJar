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
 * @author ���ۺ�
 *
 * @features �����б�
 */
class PlayMusicListFragment : BaseFragment<FragmentPlayMusicListBinding, PlayMusicListModel>() {
    private lateinit var adapter: BaseAdapter<MusicList, *>

    override fun onBackClickListener() {
        requireActivity().finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        initTitleBar("�����б�")
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
                is PermissionResult.Grant -> {  //Ȩ������
                    Log.e("����", "Ȩ������")
                    model.queryMusicList(requireContext())
                }
                is PermissionResult.Rationale -> {  //Ȩ�޾ܾ�
                    it.permissions.forEach { s ->
                        println("Rationale:${s}")//���ܾ���Ȩ��
                    }
                    Log.e("����", "Ȩ�޾ܾ�")
                }
                is PermissionResult.Deny -> {   //Ȩ�޾ܾ����ҹ�ѡ�˲���ѯ��
                    it.permissions.forEach { s ->
                        println("deny:${s}")//���ܾ���Ȩ��
                    }
                    Log.e("����", "Ȩ�޾ܾ����ҹ�ѡ�˲���ѯ��")
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
            Log.e("����", Gson().toJson(it))
            adapter.addAllData(it)
        })

        adapter.clickEvent = { _, _, position ->
            navigationPlayMusicFragment(position)
        }
    }
}