package com.common.jar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.common.tool.base.BaseViewModel
import com.common.tool.data.Banner
import com.common.tool.proxy.CommonProxy
import kotlinx.coroutines.launch

/**
 * @author 李雄厚
 *
 * @features 测试访问
 */
class MainViewModel : BaseViewModel() {

    val success = MutableLiveData<MutableList<Banner>>()

    fun getBanner() {
        viewModelScope.launch {
            CommonProxy.proxy.banner({
                success.postValue(it)
            }, {
                toastLiveData.postValue(it.message)
            })
        }
    }
}