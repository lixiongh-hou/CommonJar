package com.common.tool.util.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * @author 李雄厚
 *
 * @features 动态权限请求
 */
object PermissionUtil {
    /**
     * 请求码
     */
    private const val RESULT_CODE = 7001

    /**
     * 权限允许
     */
    private var grant: (() -> Unit)? = null
    /**
     * 权限拒绝
     */
    private var rationale: (() -> Unit)? = null
    /**
     * 权限拒绝，且勾选了不再询问
     */
    private var deny: (() -> Unit)? = null


    /**
     * @param activity Activity
     * @param fragment Fragment
     * @param context 上下文
     * @param permission 申请的权限
     * @param grant 权限允许
     * @param deny 权限拒绝，且勾选了不再询问
     * @param rationale 权限拒绝
     *
     */
    fun requestPermission(
        fragment: Fragment? = null, activity: Activity? = null ,context: Context ,
        permission: Array<String>,
        grant: () -> Unit, deny: () -> Unit, rationale: () -> Unit
    ) {

        this.deny = deny
        this.rationale = rationale
        val tempPermission = ArrayList<String>()
        val denyPermission = ArrayList<String>()
        permission.forEach {
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(context, it) !=
                PackageManager.PERMISSION_GRANTED) {
                if (activity?.shouldShowRequestPermissionRationale(it)?:
                    fragment!!.shouldShowRequestPermissionRationale(it)) {
                    denyPermission.add(it)
                }else{
                    tempPermission.add(it)
                }
            }
        }
        if (tempPermission.isEmpty() && denyPermission.isEmpty()) {
            grant.invoke()
        }else{
            if (denyPermission.isNotEmpty()){
                deny.invoke()
            }
            if (tempPermission.isNotEmpty()){
                activity?.requestPermissions(permission, RESULT_CODE) ?:
                fragment!!.requestPermissions(permission, RESULT_CODE)
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if (requestCode == RESULT_CODE){
            val rationalePermission = ArrayList<String>()
            grantResults.forEachIndexed { index, value ->
                if (value != PackageManager.PERMISSION_GRANTED) {
                    rationalePermission.add(permissions[index])
                }
            }
            if (rationalePermission.isEmpty()){
                grant?.invoke()
            }else{
                rationale?.invoke()
            }
        }
    }


}