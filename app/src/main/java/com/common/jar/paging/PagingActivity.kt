package com.common.jar.paging

import android.os.Bundle
import androidx.navigation.Navigation
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.ActivityPagingBinding
import com.common.tool.base.BaseActivity
import com.common.tool.base.EmptyViewModel
import com.common.tool.view.FixFragmentNavigator

/**
 * @author 李雄厚
 * Paging 大致使用 https://developer.android.com/jetpack/androidx/releases/paging
 *
 * @features 主界面
 */
class PagingActivity : BaseActivity<ActivityPagingBinding, EmptyViewModel>() {
    override val layoutId: Int = R.layout.activity_paging

    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.updateLiveData.observe(this) {
            title = it
        }

        val navController = Navigation.findNavController(this, R.id.container)// get fragment

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        // setup custom navigator
        val navigator =
            FixFragmentNavigator(this, navHostFragment.childFragmentManager, R.id.container)

        navController.navigatorProvider.addNavigator(navigator)

        // set navigation graph
        navController.setGraph(R.navigation.nav_paging)
    }

    override fun initData() {
    }
}