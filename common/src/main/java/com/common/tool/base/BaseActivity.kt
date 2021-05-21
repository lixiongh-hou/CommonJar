package com.common.tool.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.common.tool.R
import com.common.tool.dialog.ReminderDialog
import com.common.tool.multi_state.EmptyState
import com.common.tool.multi_state.ErrorState
import com.common.tool.multi_state.LoadingState
import com.common.tool.multi_state.NetworkState
import com.common.tool.notify.ReminderModel
import com.common.tool.util.ToastUtil.toast
import com.dlong.netstatus.DLNetManager
import com.dlong.netstatus.annotation.NetType
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.SuccessState
import java.lang.reflect.ParameterizedType


/**
 * @author 李雄厚
 *
 * @features Activity基类
 *
 */
abstract class BaseActivity<Binding : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    private val className = this.javaClass.simpleName
    lateinit var binding: Binding
    lateinit var model: VM
    private lateinit var cl: RelativeLayout
    abstract val layoutId: Int
    private lateinit var reminderModel: ReminderModel

    private var multiStateContainer: MultiStateContainer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        setMultiStateContainer()
        initBindingWithModel()
        reminderModel = (applicationContext as BaseApp).appViewModelProvider(this)
            .get(ReminderModel::class.java)
        initView(savedInstanceState)
        setObserve()
    }


    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
    protected fun success() {
        multiStateContainer?.run { show<SuccessState>() }
    }

    /**
     * 如果使用使用请在外层包裹的View写入"@+id/multiStateId"
     *
     * TODO 1,如果不使用忽略该"multiStateId",
     * TODO 2,要自定义覆写此方法
     */
    protected open fun setMultiStateContainer() {
        val view = binding.root.findViewById<View>(R.id.multiStateId)
        if (view == null) {
            Log.e(
                "测试",
                "未找到`multiStateId`对应的View,如果使用请在外层包裹'@+id/multiStateId'---${this.javaClass.simpleName}"
            )
            return
        }
        multiStateContainer = view.bindMultiState()
    }

    private fun initBindingWithModel() {
        val type = javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = type.actualTypeArguments
        for (argument in actualTypeArguments) {
            val clazz = argument as Class<*>
            if (clazz.superclass == BaseViewModel::class.java || clazz == BaseViewModel::class.java) {
                val asSubclass = clazz.asSubclass(BaseViewModel::class.java)
                @Suppress("UNCHECKED_CAST")
                model = ViewModelProvider(this).get(asSubclass) as VM
            }
        }
        if (!::model.isInitialized) {
            throw IllegalStateException("Model 必须是BaseViewModel的子类")
        }
    }

    private fun setObserve() {
        reminderModel.reminder.observe(this) {
            ReminderDialog.show(supportFragmentManager, it)
        }
        model.toastLiveData.observe(this) {
            toast(it)
        }
        model.errorLiveData.observe(this) {
            toast(it)
            failureAfter()
            // TODO 这个地方不建议直接做判断，尽量通过code来做判断
            if (it == "网络不可用") {
                multiStateContainer?.run { show<NetworkState>() }
            } else {
                multiStateContainer?.run { show<ErrorState>() }
            }
        }

        DLNetManager.getInstance(this.application).getNetTypeLiveData().observe(this, Observer {
            if (it == NetType.NONE) {
                Log.e("测试", "没有网络$className")
                multiStateContainer?.run { show<NetworkState>() }
            } else {
                Log.e("测试", "有网络$className----$it")
                multiStateContainer?.run { show<LoadingState> { } }
                startRefresh()
                initData()
            }
        })
    }

    protected fun getTitleBar(): RelativeLayout {
        return cl
    }

    fun initTitleBar(title: String) {
        cl = binding.root.findViewById(R.id.baseTitleLay)
        val tvTitle = cl.findViewById(R.id.baseTitleText) as AppCompatTextView
        tvTitle.text = title
        (cl.findViewById(R.id.baseTitleClose) as (AppCompatImageView)).setOnClickListener {
            finish()
        }
    }

    fun toast(message: String?) {
        message ?: return
        if (message.isNullOrEmpty()) {
            return
        }
        message.toast()
    }

    /*--------------------------------刷新控件-------------------------------------------*/
    /**刷新,加载 */
    companion object {
        private const val REFRESH = 1
        private const val LOADING = 2
    }

    /**
     * 只加载一次空布局，防止重复触发
     */
    var isSuccess = false

    /**一页的数据数 */
    var pageSize = 10

    /**页数 */
    var page = 0

    /**数据长度 */
    private var dataLength = 0

    /**记录当前操作,刷新还是加载 */
    private var what: Int = REFRESH

    /**刷新布局*/
    private var mSmartRefreshLayout: SmartRefreshLayout? = null

    fun isRefresh(): Boolean {
        return what == REFRESH
    }

    /**
     * 开始刷新
     */
    open fun startRefresh() {
        isSuccess = false
        what = REFRESH
        page = 0
        refresh()
    }

    /**
     * 开始加载
     */
    open fun startLoadMore() {
        what = LOADING
        page++
        loadMore()
    }

    /**
     * 重写这两个方法只处理下拉刷新上拉加载逻辑
     */
    open fun refresh() {}
    open fun loadMore() {}

    /**
     * 初始化刷新控件
     */
    fun initRefresh() {
        mSmartRefreshLayout = binding.root.findViewById(R.id.mSmartRefreshLayout)
        mSmartRefreshLayout?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                // 开始加载
                startLoadMore()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                // 开始刷新
                startRefresh()
            }
        })
    }

    /**
     * @param isFullscreen 如果使用的页面有是列表而不需要全屏加载空布局 = false
     */
    fun successAfter(newLength: Int, isFullscreen: Boolean = true) {
        mSmartRefreshLayout?.apply {
            if (what == LOADING) {
                if (newLength - dataLength < pageSize || newLength < pageSize) {
                    // 加载成功,没有有更多数据
                    finishLoadMore(0, true, true)
                } else {
                    // 加载成功,有更多数据
                    finishLoadMore(0, true, false)
                }
            } else {
                // 刷新成功、本来老版本后面参数有个0
                finishRefresh(true)
                if (newLength < pageSize) {
                    //刷新成功没有更多数据
                    setNoMoreData(true)
                } else {
                    setNoMoreData(false)
                }
            }
        }

        dataLength = newLength
        if (isFullscreen) {
            setEmpty()
        }
    }


    /**
     * 数据请求失败后执行
     *
     */
    open fun failureAfter() {
        mSmartRefreshLayout?.apply {
            if (what == LOADING) {
                // 加载失败
                finishLoadMore(0, false, false)
                page--
            } else {
                // 刷新失败、少个0
                finishRefresh(false)
            }
        }
    }

    /**
     * 判断数据源长度,决定是否显示布局
     */
    private fun setEmpty() {
        if (!isSuccess) {
            if (dataLength > 0) {
                success()
            } else if (dataLength == 0) {
                multiStateContainer?.run { show<EmptyState>() }
            }
            isSuccess = true
        }
    }

    /**
     * 启用刷新
     */
    protected fun setEnableRefresh(enable: Boolean = true) {
        mSmartRefreshLayout?.run {
            setEnableRefresh(enable)
            setEnableOverScrollDrag(true)
        }
    }

    /**
     * 启用加载
     */
    protected fun setEnableLoadMore(enable: Boolean = true) {
        mSmartRefreshLayout?.run {
            setEnableLoadMore(enable)
            setEnableOverScrollDrag(true)
        }
    }
}
