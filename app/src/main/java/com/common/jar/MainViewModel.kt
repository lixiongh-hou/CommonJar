package com.common.jar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.common.tool.base.BaseViewModel
import com.common.tool.data.entity.ResultBody
import com.common.tool.data.proxy.CommonProxy
import kotlinx.coroutines.launch

/**
 * @author 李雄厚
 *
 * @features 测试访问
 */
class MainViewModel : BaseViewModel() {

    val success = MutableLiveData<ResultBody>()

    fun resultBody(page: String) {
        viewModelScope.launch {
            CommonProxy.proxy.resultBody(page, {
                success.postValue(it)
            }, {
                errorLiveData.postValue(it.message)
            })
        }
    }
}