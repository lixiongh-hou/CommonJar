package com.common.tool.data.entity

/**
 * @author 李雄厚
 *
 * @features 接口结果体解析
 */
data class ResultBody(
    val current_page: Int,
    val `data`: List<Data>,
    val per_page: Int,
    val total: Int
)

data class Data(
    val content: String,
    val created_at: String,
    val deleted_at: Any,
    val discuss_id: Int,
    val id: Int,
    val media: List<String>,
    val post_top: Int,
    val read_count: Int,
    val topicInfo: TopicInfo,
    val topic_id: Int,
    val type: Int,
    val uid: Int,
    val updated_at: String,
    val userInfo: UserInfo
)

data class TopicInfo(
    val bg_image: String,
    val cate_id: Int,
    val cover_image: String,
    val created_at: String,
    val deleted_at: Any,
    val description: String,
    val id: Int,
    val topic_name: String,
    val uid: Int,
    val updated_at: String
)

data class UserInfo(
    val avatar: String,
    val uid: Int,
    val username: String
)