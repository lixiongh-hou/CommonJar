package com.common.jar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.common.jar.databinding.ActivityMainBinding
import com.common.tool.dialog.ReminderDialog
import com.common.jar.paging.PagingActivity
import com.common.jar.reminder.ReminderActivity
import com.common.jar.work.WorkActivity
import com.common.tool.base.BaseActivity
import com.google.gson.Gson

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        binding.activity = this
    }

    override fun initData() {
        model.getBanner()

        model.success.observe(this, Observer {
            binding.content = Gson().toJson(it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "选择图片")
        menu?.add(0, 2, 0, "分页加载")
        menu?.add(0, 3, 0, "闹钟提醒")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> startActivity(Intent(this, WorkActivity::class.java))
            2 -> startActivity(Intent(this, PagingActivity::class.java))
            3 -> startActivity(Intent(this, ReminderActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun workerLiveData(activity: AppCompatActivity) {
//        NotifyManager.workManager.getWorkInfosByTagLiveData(NotifyManager.WORKER_TAG).observe(activity, Observer {
//            if (it.isNullOrEmpty()) {
//                return@Observer
//            }
//            it.forEach { workInfo ->
//                /*
//                会保存上传执行的状态，包括数据
//                不管是否杀死程序
//                *  */
//                when (workInfo?.state) {
//                    WorkInfo.State.BLOCKED -> Log.e("测试", "堵塞")
//                    WorkInfo.State.RUNNING -> Log.e("测试", "正在运行")
//                    WorkInfo.State.ENQUEUED -> Log.e("测试", "任务入队")
//                    WorkInfo.State.CANCELLED -> Log.e("测试", "取消")
//                    WorkInfo.State.FAILED -> Log.e("测试", "失败")
//                    WorkInfo.State.SUCCEEDED -> {
//                        val data = workInfo.outputData
//                        val reminderId = data.getString("reminderId") ?: ""
//                        val name = data.getString("name") ?: ""
//                        val time = data.getString("time") ?: ""
//                        Log.e("测试", "worker执行成功,,reminderId,$reminderId,,name,$name,,time$time")
//                        ReminderDialog.show(supportFragmentManager, name)
//                    }
//                }
//            }
//
//        })
//    }

}
