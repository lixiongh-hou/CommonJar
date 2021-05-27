package com.common.jar.paging.server

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author 李雄厚
 *
 * @features 服务器配置
 */
interface CommonService {
    /**
     * 写入自己的服务器，这只是个例子
     */
    @GET("article/list/{page}/json")
    suspend fun getArticle(@Path("page") page: String): BaseResponse<ArticleBean>
}