package com.common.tool.data.exception

import java.lang.Exception


/**
 * @author 李雄厚
 *
 * @features 服务器错误提示
 */
class ServerException(val code: Int, message: String): Exception(message) {
    constructor(error: ApiError):this(error.code,error.message)
}