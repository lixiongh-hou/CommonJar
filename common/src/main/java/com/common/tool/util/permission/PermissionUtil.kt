package com.common.tool.util.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * @author ���ۺ�
 *
 * @features ��̬Ȩ������
 */
object PermissionUtil {
    /**
     * ������
     */
    private const val RESULT_CODE = 7001

    /**
     * Ȩ������
     */
    private var grant: (() -> Unit)? = null
    /**
     * Ȩ�޾ܾ�
     */
    private var rationale: (() -> Unit)? = null
    /**
     * Ȩ�޾ܾ����ҹ�ѡ�˲���ѯ��
     */
    private var deny: (() -> Unit)? = null


    /**
     * @param activity Activity
     * @param fragment Fragment
     * @param context ������
     * @param permission �����Ȩ��
     * @param grant Ȩ������
     * @param deny Ȩ�޾ܾ����ҹ�ѡ�˲���ѯ��
     * @param rationale Ȩ�޾ܾ�
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
            //�ж��Ƿ���Ȩ��
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