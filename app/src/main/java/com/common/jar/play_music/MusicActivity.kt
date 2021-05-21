package com.common.jar.play_music

import android.os.Bundle
import androidx.navigation.Navigation
import com.common.jar.R
import com.common.jar.databinding.ActivityPlayMusicBinding
import com.common.tool.base.BaseActivity
import com.common.tool.base.EmptyViewModel
import com.common.tool.view.FixFragmentNavigator

/**
 * @author ¿Ó–€∫Ò
 *
 * @features ***
 */
class MusicActivity : BaseActivity<ActivityPlayMusicBinding, EmptyViewModel>() {

    override val layoutId: Int = R.layout.activity_music

    override fun initView(savedInstanceState: Bundle?) {

        val navController = Navigation.findNavController(this, R.id.container)// get fragment

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        // setup custom navigator
        val navigator =
            FixFragmentNavigator(this, navHostFragment.childFragmentManager, R.id.container)

        navController.navigatorProvider.addNavigator(navigator)

        // set navigation graph
        navController.setGraph(R.navigation.nav_play_music)
    }

    override fun initData() {
    }
}