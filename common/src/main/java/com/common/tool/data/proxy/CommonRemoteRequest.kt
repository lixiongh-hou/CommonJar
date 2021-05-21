package com.common.tool.data.proxy

import com.common.tool.data.DataConvert.convert
import com.common.tool.data.DataConvert.convertNetworkError
import com.common.tool.data.HttpClient
import com.common.tool.data.entity.ResultBody
import com.common.tool.data.exception.ApiError
import com.common.tool.data.exception.ServerException
import com.common.tool.data.proxy.ICommonRequest.Companion.runInDispatcherIO
import com.common.tool.util.NetworkUtil

/**
 * @author 李雄厚
 *
 * @features 远程访问
 */
internal class CommonRemoteRequest : ICommonRequest {

    private inline fun checkNetwork(error: (ApiError) -> Unit, reject: () -> Unit) {
        if (!NetworkUtil.isNetworkConnected()) {
            error.invoke(ApiError(-2, "网络不可用"))
            reject.invoke()
        }
    }


    private inline fun checkNetwork(error: (ApiError) -> Unit) {
        if (!NetworkUtil.isNetworkConnected()) {
            error.invoke(ApiError(-2, "网络不可用"))
        }
    }

    override suspend fun resultBody(page: String, success: (ResultBody) -> Unit, error: (ApiError) -> Unit) {
        checkNetwork(error) { return }
        runInDispatcherIO {
            try {
                HttpClient.getCommonService().resultBody(page)
                    .convert {
                        success.invoke(it!!)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is ServerException) {
                    error.invoke(ApiError(e.code, e.message ?: ""))
                } else {
                    e.convertNetworkError({
                        error.invoke(it)
                    }, {
                        checkNetwork(error)
                    })
                }
            }
        }
    }
}