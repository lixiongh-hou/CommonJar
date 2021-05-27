package com.common.jar.paging.server

import com.google.gson.annotations.SerializedName

/**
 * @author 李雄厚
 *
 * @features ***
 */
data class ArticleBean(
    val curPage: Int,
    /**
     * 当前数据+20条
     */
    val offset: Int,
    /**
     * 数据是否加载完成
     */
    val over: Boolean,
    /**
     * 总页数
     */
    val pageCount: Int,
    /**
     * 每页数据
     */
    val size: Int,
    /**
     * 总数据
     */
    val total: Int,

    @SerializedName("datas")
    val Articles: List<DataEntity>
)