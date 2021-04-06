package com.common.tool.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author 李雄厚
 *
 * @features Activity基类
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayout())
    }

    abstract fun initLayout(): Int

}