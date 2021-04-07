package com.common.tool.proxy

import com.common.tool.data.Banner
import com.common.tool.data.DataConvert.convertNetworkError
import com.common.tool.data.exception.ApiError
import com.common.tool.data.exception.ServerException
import com.common.tool.proxy.ICommonRequest.Companion.runInDispatcherIO

/**
 * @author 李雄厚
 *
 * @features 本地访问
 */
internal class CommonLocalRequest : ICommonRequest {
    override suspend fun banner(success: (MutableList<Banner>) -> Unit, error: (ApiError) -> Unit) {
        error.invoke(ApiError(-1,"网络不可用"))
    }

//    override suspend fun test(success: (MutableList<Banner>) -> Unit, error: (ApiError) -> Unit) {
//        runInDispatcherIO {
//            try {
//                //这里写入自己本地逻辑
//                success.invoke("访问成功")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                if (e is ServerException) {
//                    error.invoke(ApiError(e.code, e.message ?: ""))
//                } else {
//                    e.convertNetworkError {
//                        error.invoke(it)
//                    }
//                }
//            }
//        }
//    }
}