package com.common.tool.data

import com.common.tool.data.exception.ApiError
import com.common.tool.data.exception.ServerException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * @author 李雄厚
 *
 * @features 服务器错误提示转换
 */
internal object DataConvert {

    fun Throwable.convertNetworkError(failure: (ApiError) -> Unit, redirect: () -> Unit) {
        if (this is SocketTimeoutException || this is ConnectException || this is SSLHandshakeException) {
            redirect.invoke()
            return
        }
        failure(
            when (this) {
                is HttpException -> {
                    when {
                        this.code() >= 500 -> ApiError(
                            this.code(),
                            "服务器错误"
                        )
                        this.code() in 400..499 -> ApiError(
                            this.code(),
                            "客户端请求地址错误"
                        )
                        else -> {
                            ApiError(this.code(), "未知错误")
                        }
                    }
                }
                is UnknownHostException -> ApiError(-1, "域名错误")
                is NoRouteToHostException -> ApiError(-1, "网络不通")
                else -> {
                    ApiError(-1, "未知错误")
                }
            }
        )
    }

    fun Throwable.convertNetworkError(failure: (ApiError) -> Unit) {
        failure(
            when (this) {
                is SocketTimeoutException -> ApiError(
                    -1,
                    "网络请求超时"
                )
                is ConnectException -> ApiError(
                    -1,
                    "网络连接超时"
                )
                is SSLHandshakeException -> ApiError(
                    -1,
                    "安全证书异常"
                )
                is UnknownHostException -> ApiError(
                    -1,
                    "域名解析失败"
                )
                is HttpException -> {
                    when {
                        this.code() >= 500 -> ApiError(
                            this.code(),
                            "服务器错误"
                        )
                        this.code() in 400..499 -> ApiError(
                            this.code(),
                            "客户端请求地址错误"
                        )
                        else -> {
                            ApiError(
                                this.code(),
                                "未知错误"
                            )
                        }
                    }
                }
                is NoRouteToHostException -> ApiError(-1, "网络不通")
                else -> {
                    ApiError(-1, "未知错误")
                }
            }
        )
    }

    fun Throwable.convertNetworkError(failure: (ApiError) -> Unit, defaultError: String = "未知错误") {
        failure(
            when (this) {
                is SocketTimeoutException -> ApiError(
                    -1,
                    "网络请求超时"
                )
                is ConnectException -> ApiError(
                    -1,
                    "网络连接超时"
                )
                is SSLHandshakeException -> ApiError(
                    -1,
                    "安全证书异常"
                )
                is UnknownHostException -> ApiError(
                    -1,
                    "域名解析失败"
                )
                is HttpException -> {
                    when {
                        this.code() >= 500 -> ApiError(
                            this.code(),
                            "服务器错误"
                        )
                        this.code() in 400..499 -> ApiError(
                            this.code(),
                            "客户端请求地址错误"
                        )
                        else -> {
                            ApiError(
                                this.code(),
                                defaultError
                            )
                        }
                    }
                }
                is NoRouteToHostException -> ApiError(-1, "网络不通")
                else -> {
                    ApiError(-1, defaultError)
                }
            }
        )
    }

    private fun <T> BaseResponse<T>.convertErrorMessage(defaultError: String = "请求失败"): ApiError {
        //根据后台给的字段自行修改
        return when (this.errorCode) {
            100 -> ApiError(this.errorCode, "没有绑定设备")
            else -> ApiError(this.errorCode, this.errorMsg ?: defaultError)
        }
    }

    fun <T> BaseResponse<T>.convert(success: (T?) -> Unit) {
        if (this.success()) {
            success(this.data)
        } else {
            throw ServerException(convertErrorMessage())
        }
    }
}