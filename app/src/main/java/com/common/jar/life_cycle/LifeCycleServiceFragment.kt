package com.common.jar.life_cycle

import android.content.Intent
import android.os.Bundle
import com.common.jar.databinding.FragmentLifeCycleServiceBinding
import com.common.jar.life_cycle.service.MyMusicService
import com.common.tool.base.BaseFragment
import com.common.tool.base.EmptyViewModel

/**
 * @author 李雄厚
 *
 * @features ***
 */
class LifeCycleServiceFragment : BaseFragment<FragmentLifeCycleServiceBinding, EmptyViewModel>() {

    override fun onBackClickListener() {
        requireActivity().finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.activity = this
    }

    override fun initData() {

    }

    fun starService() {
        requireActivity().startService(Intent(requireContext(), MyMusicService::class.java))

    }

    fun stopService() {
        requireActivity().stopService(Intent(requireContext(), MyMusicService::class.java))
    }
}