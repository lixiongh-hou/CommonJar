package com.common.tool.data.service

import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * @author 李雄厚
 *
 * @features 接口地址
 */
internal object Url{
    const val Banner = "banner/json"/*自己接口地址*/


    internal fun JsonObject.convertRequestBody(mediaType: MediaType? = ConvertMediaType.JSON) = this.toString().toRequestBody(mediaType)

    internal object ConvertMediaType {
        val JSON = "application/json; charset=UTF-8".toMediaTypeOrNull()
    }
}