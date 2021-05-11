package com.common.tool.network_status

/**
 * @author 李雄厚
 *
 * @features ***
 */
interface NetStateChangeObserver {
    fun onNetDisconnected()
    fun onNetConnected(networkType: NetworkType)
}