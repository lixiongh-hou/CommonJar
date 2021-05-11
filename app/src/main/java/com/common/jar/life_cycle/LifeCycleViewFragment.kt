package com.common.jar.life_cycle

import android.os.Bundle
import com.common.jar.databinding.FragmentLifeCycleViewBinding
import com.common.tool.base.BaseFragment
import com.common.tool.base.EmptyViewModel

/**
 * @author 李雄厚
 *
 * @features  LifeCycle绑定组件使用
 */
class LifeCycleViewFragment : BaseFragment<FragmentLifeCycleViewBinding, EmptyViewModel>() {

    override fun initView(savedInstanceState: Bundle?) {
        lifecycle.addObserver(binding.chronometer)

    }

    override fun initData() {
    }
}