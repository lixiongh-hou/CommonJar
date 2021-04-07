package com.common.tool.data.service

import com.common.tool.data.Banner
import com.common.tool.data.BaseResponse
import retrofit2.http.GET

/**
 * @author 李雄厚
 *
 * @features 服务器配置
 */
interface CommonService {
    /**
     * 写入自己的服务器，这只是个例子
     */
    @GET(Url.Banner)
    suspend fun banner(): BaseResponse<MutableList<Banner>>
//    @POST(Url.Banner)
//  suspend fun banner(@Body body: RequestBody): BaseResponse<MutableList<Banner>>
}