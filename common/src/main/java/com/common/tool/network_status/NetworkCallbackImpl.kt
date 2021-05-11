package com.common.tool.network_status

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

/**
 * @author 李雄厚
 *
 * @features ***
 */
class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
//        Log.e("测试", "onAvailable: 网络已连接");
    }

    override fun onLost(network: Network) {
        super.onLost(network)
//        Log.e("测试", "onLost: 网络已断开")
        notifyObservers(NetworkType.NETWORK_NO)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val netType: NetworkType
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            netType = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.e("测试", "onCapabilitiesChanged: 网络类型为wifi")
                    NetworkType.NETWORK_WIFI
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
//                    Log.e("测试", "onCapabilitiesChanged: 蜂窝网络")
                    NetworkType.NETWORK_4G
                }
                else -> {
//                    Log.e("测试", "onCapabilitiesChanged: 其他网络")
                    NetworkType.NETWORK_UNKNOWN
                }
            }
            notifyObservers(netType)
        }

    }

    private fun notifyObservers(networkType: NetworkType) {
        if (networkType == NetworkType.NETWORK_NO) {
            mObservers.forEach {
                it.onNetDisconnected()
            }
        } else {
            mObservers.forEach {
                it.onNetConnected(networkType)
            }
        }
    }

    private val mObservers: MutableList<NetStateChangeObserver> = ArrayList()

    companion object {
        private val INSTANCE by lazy {
            NetworkCallbackImpl()
        }

        fun registerNetwork(context: Context) {
            val builder = NetworkRequest.Builder()
            val request = builder.build()
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connMgr.registerNetworkCallback(request, INSTANCE)
        }

        fun unRegisterNetwork(context: Context) {
            val systemService =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            systemService.unregisterNetworkCallback(INSTANCE)
        }

        fun registerObserver(observer: NetStateChangeObserver?) {
            if (observer == null) {
                return
            }
            if (!INSTANCE.mObservers.contains(observer)) {
                INSTANCE.mObservers.add(observer)
            }
        }

        fun unRegisterObserver(observer: NetStateChangeObserver?) {
            if (observer == null) {
                return
            }
            INSTANCE.mObservers.remove(observer)
        }
    }

}