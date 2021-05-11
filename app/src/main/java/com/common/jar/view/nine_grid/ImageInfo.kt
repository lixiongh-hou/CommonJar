package com.common.jar.view.nine_grid

import java.io.Serializable

/**
 * @author 李雄厚
 *
 * @features ***
 */
data class ImageInfo(
    val thumbnailUrl: String = "",
    val bigImageUrl: String = "",
    val imageViewHeight: Int = 0,
    val imageViewWidth: Int = 0,
    val imageViewX: Int = 0,
    val imageViewY: Int = 0

) : Serializable {
    override fun toString(): String {
        return "ImageInfo(thumbnailUrl='$thumbnailUrl', bigImageUrl='$bigImageUrl', imageViewHeight=$imageViewHeight, imageViewWidth=$imageViewWidth, imageViewX=$imageViewX, imageViewY=$imageViewY)"
    }
}
