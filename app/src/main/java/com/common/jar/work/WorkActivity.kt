package com.common.jar.work

import android.os.Bundle
import androidx.navigation.Navigation
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.ActivityWorkBinding
import com.common.tool.base.BaseActivity
import com.common.tool.base.BaseViewModel
import com.common.tool.view.FixFragmentNavigator

/**
 * @author 李雄厚
 * WorkManager 大致使用 https://developer.android.google.cn/topic/libraries/architecture/workmanager
 *
 * @features 主界面
 */
class WorkActivity: BaseActivity<ActivityWorkBinding, BaseViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_work

    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.updateLiveData.observe(this){
            title = it
        }
        val navController = Navigation.findNavController(this, R.id.container)// get fragment

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        // setup custom navigator
        val navigator = FixFragmentNavigator(this, navHostFragment.childFragmentManager, R.id.container)

        navController.navigatorProvider.addNavigator(navigator)

        // set navigation graph
        navController.setGraph(R.navigation.nav_work)

    }

    override fun initData() {
    }
}