package com.common.tool.util.permission

/**
 * @author 李雄厚
 *
 * @features 权限结果
 */
sealed class PermissionResult {
    /**
     * 授予权限
     */
    object Grant : PermissionResult()

    /**
     * 拒绝权限，不再询问
     */
    class Deny(val permissions: Array<String>) : PermissionResult()

    /**
     * 不授予权限
     */
    class NoGrant(val permissions: Array<String>) : PermissionResult()

    /**
     * 权限检测
     */
    class PermissionCheck(val permissions: Array<String>) : PermissionResult()


}