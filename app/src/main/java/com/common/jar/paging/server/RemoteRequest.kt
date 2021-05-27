package com.common.jar.paging.server

import com.common.tool.data.exception.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李雄厚
 *
 * @features ***
 */
object RemoteRequest {

    suspend fun getArticle(page: Int, success: (ArticleBean) -> Unit, error: (ApiError) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                HttpClient.getCommonService().getArticle(page.toString()).convert {
                    success.invoke(it!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error.invoke(ApiError(10000, e.toString()))
            }
        }

    }
}

fun <T> BaseResponse<T>.convert(success: (T?) -> Unit) {
    if (this.success()) {
        success(this.data)
    }
}