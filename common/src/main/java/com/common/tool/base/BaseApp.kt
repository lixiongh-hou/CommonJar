package com.common.tool.base

import android.app.Activity
import android.app.Application
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.common.tool.R
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.constant.SpinnerStyle


/**
 * @author 李雄厚
 *
 * @features Application基类
 */
open class BaseApp : Application(), ViewModelStoreOwner {

    private val factory: ViewModelProvider.Factory by lazy {
        return@lazy ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }
    private var appViewModelStore: ViewModelStore? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        appViewModelStore = ViewModelStore()
    }

    companion object {
        lateinit var instance: Application private set

        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setHeaderHeight(50F).setFooterHeight(50F)
                    // 设置主题颜色
                    .setPrimaryColorsId(R.color.white, R.color.color_90)
                    // 在内容不满一页的时候，是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(true)
                    // 是否在加载更多完成之后滚动内容显示新数据
                    .setEnableScrollContentWhenLoaded(true)
                    // 是否启用越界回弹
                    .setEnableOverScrollBounce(false)
                    // 设置回弹动画时长
                    .setReboundDuration(250)
                    // 设置回弹显示插值器
                    .setReboundInterpolator(LinearInterpolator())
                ClassicsHeader(context)
                    .setSpinnerStyle(SpinnerStyle.Translate)
                    .setDrawableArrowSize(16F)
                    .setDrawableMarginRight(10F)
                    .setDrawableProgressSize(16F)
                    .setEnableLastTime(true).setTextSizeTime(10F).setTextTimeMarginTop(2F)
                    .setFinishDuration(400)
                    .setTextSizeTitle(14F)
                    .setAccentColor(ContextCompat.getColor(context, R.color.color_90))
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                ClassicsFooter(context)
                    .setSpinnerStyle(SpinnerStyle.Translate)
                    .setDrawableArrowSize(16F)
                    .setDrawableMarginRight(10F)
                    .setDrawableProgressSize(16F)
                    .setFinishDuration(400)
                    .setTextSizeTitle(14F)
                    .setAccentColor(ContextCompat.getColor(context, R.color.color_90))
                    .setPrimaryColor(ContextCompat.getColor(context, R.color.color_F4))
            }
        }
    }

    fun appViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(activity.applicationContext as BaseApp, factory)
    }

    fun appViewModelProvider(activity: Application): ViewModelProvider {
        return ViewModelProvider(activity.applicationContext as BaseApp, factory)
    }

    override fun getViewModelStore(): ViewModelStore {
        if (appViewModelStore == null) {
            appViewModelStore = ViewModelStore()
        }
        return appViewModelStore as ViewModelStore
    }

}