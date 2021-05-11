package com.common.tool.data.service

import com.common.tool.data.BaseResponse
import com.common.tool.data.entity.ResultBody
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author 李雄厚
 *
 * @features 服务器配置
 */
interface CommonService {
    /**
     * 写入自己的服务器，这只是个例子
     */
    @GET(Url.URL)
    suspend fun resultBody(@Query("page") page: String): BaseResponse<ResultBody>
}