package com.common.jar

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.common.jar.databinding.ActivityMainBinding
import com.common.tool.base.BaseActivity
import com.google.gson.Gson

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        model.getBanner()

        model.success.observe(this, Observer {
            Log.e("测试", "访问成功")
            binding.content = Gson().toJson(it)
        })
    }

}