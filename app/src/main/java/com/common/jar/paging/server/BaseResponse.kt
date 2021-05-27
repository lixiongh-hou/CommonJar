package com.common.jar.paging.server

/**
 * @author 李雄厚
 *
 * @features 后台通用接收体
 *
 */
data class BaseResponse<T>(val errorCode: Int, val errorMsg: String?, val data: T?) {
    fun success(): Boolean {
        //根据后台给的字段自行修改
        return errorCode == 0
    }
}