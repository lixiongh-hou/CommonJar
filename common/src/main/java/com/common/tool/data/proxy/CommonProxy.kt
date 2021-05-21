package com.common.tool.data.proxy

import com.common.tool.util.NetworkUtil.isNetworkConnected
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author 李雄厚
 *
 * @features 服务器代理
 */
class CommonProxy : InvocationHandler {

    companion object{
        val proxy by lazy {
            CommonProxy().internalProxy
        }
    }

    private val remote by lazy { CommonRemoteRequest() }
    private val local by lazy { CommonLocalRequest() }
    private val internalProxy by lazy {
        Proxy.newProxyInstance(
            CommonProxy::class.java.classLoader,
            arrayOf(ICommonRequest::class.java),
            this
        ) as ICommonRequest
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        return if (isNetworkConnected()){
            method.invoke(remote, * (args ?: arrayOfNulls<Any>(0)))
        }else{
            method.invoke(local, * (args ?: arrayOfNulls<Any>(0)))
        }
    }
}