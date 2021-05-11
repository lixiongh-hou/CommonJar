package com.common.jar

import android.graphics.Bitmap
import coil.request.Parameters

/**
 * @author 李雄厚
 *
 * @features 解析成本地数据
 */
data class VoResultBody(
    val avatar: String,
    val name: String,
    val time: String,
    val content: String,
    val pic: MutableList<Pic>
)

data class Pic(
    var width: Int,
    var height: Int,
    var pic: String,
    val resource: Bitmap?,
    val parameters: Parameters = Parameters.EMPTY
)