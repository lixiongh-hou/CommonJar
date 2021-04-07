package com.common.tool.proxy

import com.common.tool.data.Banner
import com.common.tool.data.exception.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李雄厚
 *
 * @features ***
 */
interface ICommonRequest {
    companion object{
        suspend fun runInDispatcherIO(block: suspend () -> Unit){
            withContext(Dispatchers.IO){
                block.invoke()
            }
        }
    }

    suspend fun banner(success: (MutableList<Banner>) -> Unit, error: (ApiError) -> Unit)
}