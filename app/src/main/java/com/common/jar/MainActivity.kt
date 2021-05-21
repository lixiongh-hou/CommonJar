package com.common.jar

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.common.jar.databinding.ActivityMainBinding
import com.common.jar.life_cycle.LifeCycleActivity
import com.common.jar.paging.PagingActivity
import com.common.jar.play_music.MusicActivity
import com.common.jar.reminder.ReminderActivity
import com.common.jar.view.nine_grid.NineGridView
import com.common.jar.work.WorkActivity
import com.common.tool.base.BaseActivity
import com.common.tool.base.BaseApp
import com.common.tool.base.rv.BaseAdapter
import com.common.tool.util.solveNestQuestion
import com.google.gson.Gson


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var adapter: BaseAdapter<VoResultBody, *>
    override val layoutId: Int
        get() = R.layout.activity_main


    override fun initView(savedInstanceState: Bundle?) {
        initRefresh()
        binding.activity = this

        adapter = ResultBodyAdapter(this)
        binding.adapter = adapter
        binding.recyclerView.solveNestQuestion()

        NineGridView.setImageLoader(object : NineGridView.ImageLoader {
            override fun onDisplayImage(
                context: Context,
                imageView: AppCompatImageView,
                url: String
            ) {
                imageView.load(url) {
                    placeholder(ColorDrawable(randomColor()))
                    transformations(RoundedCornersTransformation(8F))
                }
            }

            override fun getCacheImage(url: String): Bitmap? = null

        })
        pageSize = 6
    }

    override fun initData() {

    }

    override fun refresh() {
        model.resultBody(page.toString())
        model.success.observe(this, Observer {
            val list = mutableListOf<VoResultBody>()
            it.data.forEach { data ->
                val pic = mutableListOf<Pic>()
                data.media.forEach { media ->
                    val request = ImageRequest.Builder(this)
                        .data(media)
                        .target(
                            onSuccess = { result ->
                                pic.add(
                                    Pic(
                                        drawableToBitmap(result)?.width ?: 0,
                                        drawableToBitmap(result)?.height ?: 0,
                                        media,
                                        drawableToBitmap(result)
                                    )
                                )
                            }
                        )
                        .build()
                    this.imageLoader.enqueue(request)
                }
                list.add(
                    VoResultBody(
                        data.userInfo.avatar,
                        data.userInfo.username,
                        data.created_at,
                        data.content,
                        pic
                    )
                )

            }
            Handler(Looper.myLooper()!!).postDelayed({
                Log.e("测试", Gson().toJson(list))
                if (isRefresh()) {
                    adapter.refreshData(list)
                } else {
                    adapter.addAllData(list)
                }
                successAfter(adapter.itemCount)
            }, 900)

        })
    }

    override fun loadMore() {
        model.resultBody(page.toString())
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        return null
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "选择图片")
        menu?.add(0, 2, 0, "分页加载")
        menu?.add(0, 3, 0, "闹钟提醒")
        menu?.add(0, 4, 0, "LifeCycle")
        menu?.add(0, 5, 0, "音乐播放")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> startActivity(Intent(this, WorkActivity::class.java))
            2 -> startActivity(Intent(this, PagingActivity::class.java))
            3 -> startActivity(Intent(this, ReminderActivity::class.java))
            4 -> startActivity(Intent(this, LifeCycleActivity::class.java))
            5 -> startActivity(Intent(this, MusicActivity::class.java))
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
