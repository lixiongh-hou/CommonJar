package com.common.tool.data

/**
 * @author 李雄厚
 *
 * @features 后台通用接收体
 *
 * @param code 根据后台给的字段自行修改
 * @param msg 根据后台给的字段自行修改
 */
data class BaseResponse<T>(val errorCode: Int, val errorMsg: String?, val data: T?) {
    fun success(): Boolean {
        //根据后台给的字段自行修改
        return errorCode == 0
    }
}