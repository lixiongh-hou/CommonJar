package com.common.tool.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.common.tool.dialog.ReminderDialog
import com.common.tool.notify.ReminderModel
import com.common.tool.util.ToastUtil.toast
import java.lang.reflect.ParameterizedType

/**
 * @author 李雄厚
 *
 * @features Activity基类
 *
 */
abstract class BaseActivity<Binding : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    lateinit var binding: Binding
    lateinit var model: VM
    abstract val layoutId: Int
    var index = 0

    lateinit var reminderModel: ReminderModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        initBindingWithModel()
        reminderModel = (applicationContext as BaseApp).appViewModelProvider(this).get(ReminderModel::class.java)
        reminderModel.reminder.observe(this){
            ReminderDialog.show(supportFragmentManager, it)
        }
        model.toastLiveData.observe(this) {
            toast(it)
        }
        initView(savedInstanceState)
        initData()

    }


    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()


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


    fun toast(message: String?) {
        message ?: return
        if (message.isNullOrEmpty()) {
            return
        }
        message.toast()
    }
}