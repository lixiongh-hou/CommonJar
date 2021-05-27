package com.common.jar.paging.server


/**
 * @author 李雄厚
 *
 * @features 统一数据远程实体类
 */
data class DataEntity(
    val apkLink: String?,
    val audit: Int?,
    val originId: Int = 1,
    val author: String?,
    val canEdit: Boolean?,
    val chapterId: Int?,
    val chapterName: String?,
    val collect: Boolean?,
    val courseId: Int?,
    val desc: String?,
    val descMd: String?,
    val envelopePic: String?,
    var fresh: Boolean?,
    val id: Int?,
    val link: String?,
    val niceDate: String?,
    val niceShareDate: String?,
    val origin: String?,
    val prefix: String?,
    val projectLink: String?,
    val publishTime: Long?,
    val realSuperChapterId: Int?,
    val selfVisible: Int?,
    val shareDate: Long?,
    val shareUser: String?,
    val superChapterId: Int?,
    val superChapterName: String?,
    val tags: List<Tag>?,
    val title: String?,
    val type: Int?,
    val userId: Int?,
    val visible: Int?,
    val zan: Int?,
    var top: Boolean = false
)

data class Tag(
    val name: String,
    val url: String
)