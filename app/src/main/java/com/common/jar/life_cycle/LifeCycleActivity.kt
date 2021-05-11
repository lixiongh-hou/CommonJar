package com.common.jar.life_cycle

import android.os.Bundle
import androidx.navigation.Navigation
import com.common.jar.R
import com.common.jar.databinding.ActivityLifeCycleBinding
import com.common.tool.base.BaseActivity
import com.common.tool.base.EmptyViewModel
import com.common.tool.view.FixFragmentNavigator

/**
 * @author 李雄厚
 *
 * @features ***
 */
class LifeCycleActivity: BaseActivity<ActivityLifeCycleBinding, EmptyViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_life_cycle

    override fun initView(savedInstanceState: Bundle?) {
        val navController = Navigation.findNavController(this, R.id.container)// get fragment

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        // setup custom navigator
        val navigator = FixFragmentNavigator(this, navHostFragment.childFragmentManager, R.id.container)

        navController.navigatorProvider.addNavigator(navigator)

        // set navigation graph
        navController.setGraph(R.navigation.nav_life_cycle)
    }

    override fun initData() {
    }
}