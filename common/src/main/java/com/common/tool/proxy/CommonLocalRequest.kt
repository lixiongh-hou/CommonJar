package com.common.tool.proxy

import com.common.tool.data.entity.ResultBody
import com.common.tool.data.exception.ApiError

/**
 * @author 李雄厚
 *
 * @features 本地访问
 */
internal class CommonLocalRequest : ICommonRequest {
    override suspend fun resultBody(page: String, success: (ResultBody) -> Unit, error: (ApiError) -> Unit) {
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