package com.common.tool.network_status

/**
 * @author 李雄厚
 *
 * @features 网络状态
 */
enum class NetworkType(val desc: String) {

    NETWORK_WIFI("WiFi"),
    NETWORK_4G("4G"),
    NETWORK_2G("2G"),
    NETWORK_3G("3G"),
    NETWORK_UNKNOWN("Unknown"),
    NETWORK_NO("No network");
}