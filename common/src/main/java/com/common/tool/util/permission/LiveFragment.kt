package com.common.tool.util.permission

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

/**
 * @author 李雄厚
 *
 * @features 统一权限申请回调页面
 */
internal class LiveFragment : Fragment() {

    val liveData by lazy {
        MutableLiveData<PermissionResult>()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 7001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun requestPermissions(permissions: Array<out String>) {
        val tempPermission = ArrayList<String>()
        permissions.forEach {
            if (activity?.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                tempPermission.add(it)
            }
        }

        if (tempPermission.isEmpty()) {
            liveData.value = PermissionResult.Grant
        } else {
            requestPermissions(tempPermission.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val denyPermission = ArrayList<String>()
            val rationalePermission = ArrayList<String>()
            for ((index, value) in grantResults.withIndex()) {
                if (value == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permissions[index])) {
                        rationalePermission.add(permissions[index])
                    } else {
                        denyPermission.add(permissions[index])
                    }
                }
            }
            if (denyPermission.isEmpty() && rationalePermission.isEmpty()) {
                liveData.value = PermissionResult.Grant
            } else {
                if (rationalePermission.isNotEmpty()) {
                    liveData.value = PermissionResult.NoGrant(rationalePermission.toTypedArray())
                } else if (denyPermission.isNotEmpty()) {
                    liveData.value = PermissionResult.Deny(denyPermission.toTypedArray())
                }
            }

        }
    }

    /**
     * 权限检测
     */
    fun permissionCheck(permissions: Array<out String>) {
        val tempPermission = ArrayList<String>()
        permissions.forEach {
            if (activity?.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                tempPermission.add(it)
            }
        }
        if (tempPermission.isNotEmpty()) {
            liveData.value = PermissionResult.PermissionCheck(tempPermission.toTypedArray())
        }else{
            liveData.value = PermissionResult.Grant
        }
    }
}