package com.common.tool.proxy

import com.common.tool.data.Banner
import com.common.tool.data.DataConvert.convert
import com.common.tool.data.DataConvert.convertNetworkError
import com.common.tool.data.HttpClient
import com.common.tool.data.exception.ApiError
import com.common.tool.data.exception.ServerException
import com.common.tool.proxy.ICommonRequest.Companion.runInDispatcherIO
import com.common.tool.util.NetworkUtil

/**
 * @author 李雄厚
 *
 * @features 远程访问
 */
internal class CommonRemoteRequest : ICommonRequest {

    private inline fun checkNetwork(error: (ApiError) -> Unit) {
        if (!NetworkUtil.isNetworkConnected()) {
            error.invoke(ApiError(-2, "网络不可用"))
        }
    }

    override suspend fun banner(success: (MutableList<Banner>) -> Unit, error: (ApiError) -> Unit) {
        runInDispatcherIO {
            try {
                HttpClient.getCommonService().banner()
                    .convert {
                        success.invoke(it!!)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is ServerException) {
                    error.invoke(ApiError(e.code, e.message ?: ""))
                } else {
                    e.convertNetworkError ({
                        error.invoke(it)
                    }, {
                        checkNetwork(error)
                    })
                }
            }
        }
    }
}