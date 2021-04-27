package com.common.jar.reminder

import android.os.Bundle
import android.view.MotionEvent
import androidx.navigation.Navigation
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.ActivityReminderBinding
import com.common.tool.base.BaseActivity
import com.common.tool.base.EmptyViewModel
import com.common.tool.view.FixFragmentNavigator

/**
 * @author 李雄厚
 *
 * @features 主界面
 */
class ReminderActivity : BaseActivity<ActivityReminderBinding, EmptyViewModel>() {
    var touchEvent: ((MotionEvent?) -> Unit)? = null
    override val layoutId: Int
        get() = R.layout.activity_reminder

    override fun initView(savedInstanceState: Bundle?) {
        val navController = Navigation.findNavController(this, R.id.container)// get fragment

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        // setup custom navigator
        val navigator =
            FixFragmentNavigator(this, navHostFragment.childFragmentManager, R.id.container)

        navController.navigatorProvider.addNavigator(navigator)

        // set navigation graph
        navController.setGraph(R.navigation.nav_reminder)

    }

    override fun initData() {

    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        touchEvent?.invoke(ev)
        return super.dispatchTouchEvent(ev)
    }


}