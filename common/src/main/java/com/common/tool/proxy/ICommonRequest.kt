package com.common.tool.proxy

import com.common.tool.data.entity.ResultBody
import com.common.tool.data.exception.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李雄厚
 *
 * @features 访问控制器
 */
interface ICommonRequest {
    companion object {
        suspend fun runInDispatcherIO(block: suspend () -> Unit) {
            withContext(Dispatchers.IO) {
                block.invoke()
            }
        }
    }

    suspend fun resultBody(page: String, success: (ResultBody) -> Unit, error: (ApiError) -> Unit)
}