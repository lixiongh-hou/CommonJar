package com.common.tool.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.common.tool.R
import com.common.tool.multi_state.ErrorState
import com.common.tool.multi_state.NetworkState
import com.common.tool.notify.ReminderModel
import com.common.tool.util.ToastUtil.toast
import com.dlong.netstatus.DLNetManager
import com.dlong.netstatus.annotation.NetType
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.SuccessState
import java.lang.reflect.ParameterizedType

/**
 * @author 李雄厚
 *
 * @features Fragment基类
 */
abstract class BaseFragment<Binding : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    private val className = this.javaClass.simpleName
    lateinit var binding: Binding
    lateinit var model: VM
    private lateinit var cl: RelativeLayout
    var activity: AppCompatActivity? = null
    var initData = false

    lateinit var reminderModel: ReminderModel

    var multiStateContainer: MultiStateContainer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderModel =
            (activity?.applicationContext as BaseApp).appViewModelProvider(requireActivity())
                .get(ReminderModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBindingWithModel(inflater)
//        reminderModel.reminder.observe(viewLifecycleOwner){
//            ReminderDialog.show(childFragmentManager, it)
//        }
        setMultiStateContainer()
        return binding.root
    }

    private fun initBindingWithModel(inflater: LayoutInflater) {
        val type = javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = type.actualTypeArguments
        for (argument in actualTypeArguments) {
            val clazz = argument as Class<*>
            if (clazz.superclass == ViewDataBinding::class.java) {
                val asSubclass = clazz.asSubclass(ViewDataBinding::class.java)
                val declaredMethod =
                    asSubclass.getDeclaredMethod("inflate", LayoutInflater::class.java)
                @Suppress("UNCHECKED_CAST")
                binding = declaredMethod.invoke(this, inflater) as Binding
            } else if (clazz.superclass == BaseViewModel::class.java || clazz == BaseViewModel::class.java) {
                val asSubclass = clazz.asSubclass(BaseViewModel::class.java)
                @Suppress("UNCHECKED_CAST")
                model = ViewModelProvider(this).get(asSubclass) as VM
            }
        }
        if (!::binding.isInitialized) {
            throw IllegalStateException("Binding 必须是ViewDataBinding的子类")
        }
        if (!::model.isInitialized) {
            throw IllegalStateException("Model 必须是BaseViewModel的子类")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.toastLiveData.observe(viewLifecycleOwner) {
            toast(it)

        }
        model.errorLiveData.observe(viewLifecycleOwner) {
            toast(it)
            // TODO 这个地方不建议直接做判断，尽量通过code来做判断
            if (it == "网络不可用") {
                multiStateContainer?.run { show<NetworkState>() }
            } else {
                multiStateContainer?.run { show<ErrorState>() }
            }

        }


        DLNetManager.getInstance(requireActivity().application).getNetTypeLiveData()
            .observe(viewLifecycleOwner, Observer {
                if (it == NetType.NONE) {
                    Log.e("测试", "没有网络$className")
                    multiStateContainer?.run { show<NetworkState>() }
                } else {
                    Log.e("测试", "有网络$className")
                    multiStateContainer?.run { show<SuccessState>() }
                }
                Log.e("测试", "$it---$className")
            })
        initView(savedInstanceState)
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
            Log.e("测试", "未找到`multiStateId`对应的View,如果使用请在外层包裹'@+id/multiStateId'---${this.javaClass.simpleName}")
            return
        }
        multiStateContainer = view.bindMultiState()
    }

    override fun onResume() {
        super.onResume()
        if (!initData) {
            initData()
            initData = true
        }
    }

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
        holderBackPressed()
    }

    private var callback: OnBackPressedCallback? = null
    private fun callback(): OnBackPressedCallback {
        if (callback == null) {
            callback = object : OnBackPressedCallback(true /* enabled by default */) {
                @Override
                override fun handleOnBackPressed() {
                    onBackClickListener()
                }
            }
        }
        return callback!!
    }

    open fun holderBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(this, callback())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }

    protected fun findNavController(): NavController? {
        return Navigation.findNavController(binding.root)
    }

    /**
     * 如果不使用navigate库重写该方法处理返回事件
     */
    protected open fun onBackClickListener() {
        findNavController()?.navigateUp()
    }

    protected fun getTitleBar(): RelativeLayout {
        return cl
    }

    fun initTitleBar(title: String) {
        cl = binding.root.findViewById(R.id.baseTitleLay)
        val tvTitle = cl.findViewById(R.id.baseTitleText) as AppCompatTextView
        tvTitle.text = title
        (cl.findViewById(R.id.baseTitleClose) as (AppCompatImageView)).setOnClickListener {
            onBackClickListener()
        }
    }

    fun toast(message: String?) {
        message ?: return
        if (message.isNullOrEmpty()) {
            return
        }
        message.toast()
    }

}