package com.common.tool.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.common.tool.data.bridge.EventObserver
import com.common.tool.util.ToastUtil.toast
import java.lang.reflect.ParameterizedType

/**
 * @author 李雄厚
 *
 * @features Fragment基类
 */
abstract class BaseFragment<Binding : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    lateinit var binding: Binding
    lateinit var model: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBindingWithModel(inflater)
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
        model.toastLiveData.observe(this) {
            toast(it)
        }
        initView(savedInstanceState)
        initData()
    }

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()


    fun toast(message: String?) {
        message ?: return
        if (message.isNullOrEmpty()) {
            return
        }
        message.toast()
    }

}