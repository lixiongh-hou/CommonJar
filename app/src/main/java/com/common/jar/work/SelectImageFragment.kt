package com.common.jar.work

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.FragmentSelectBinding
import com.common.tool.base.BaseFragment

private const val REQUEST_CODE_IMAGE = 100
private const val REQUEST_CODE_PERMISSIONS = 101

private const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
private const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"

/**
 * @author 李雄厚
 * WorkManager 大致使用 https://developer.android.google.cn/topic/libraries/architecture/workmanager
 *
 * @features 选择图片页面
 */
class SelectImageFragment : BaseFragment<FragmentSelectBinding, SelectImageModel>() {
    companion object {
        private val TAG = SelectImageFragment::class.java.simpleName
    }

    private var permissionRequestCount: Int = 0
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onBackClickListener() {
        requireActivity().finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.post("选择图片")
        binding.fragment = this
        savedInstanceState?.let {
            permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }
        binding.enabled = true
        //确保该应用具有运行的正确权限
        requestPermissionsIfNecessary()
    }

    override fun initData() {
    }

    /**
     * 将许可请求计数保存在轮播中
     * */

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }

    fun selectImage() {
        val chooseIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
    }

    private fun navigateBlurFragment(uri: String) {
        findNavController()?.let {
            if (it.currentDestination?.id == R.id.selectImageFragment) {
//                it.navigate(R.id.actionBlurFragment)
                it.navigate(SelectImageFragmentDirections.actionBlurFragment(uri))
            }
        }
    }

    /**
     * 必要权限申请
     */
    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1
                requestPermissions(
                    permissions,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                toast(getString(R.string.set_permissions_in_settings))
                binding.enabled = false
            }
        }
    }

    /**
     * 权限检查
     */
    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // 无操作（如果已授予权限）。
            requestPermissionsIfNecessary()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> data?.let { handleImageRequestResult(data) }
                else -> Log.e(TAG, "未知代码请求。")
            }
        } else {
            Log.e(TAG, "Unexpected Result code $resultCode。")
        }
    }

    /**
     * 处理图像请求结果
     */
    private fun handleImageRequestResult(intent: Intent) {
        //如果clipData可用，则使用它，否则我们使用数据
        val imageUri = intent.clipData?.getItemAt(0)?.uri ?: intent.data

        if (imageUri == null) {
            Log.e(TAG, "无效的输入图片Uri。")
            return
        }
        navigateBlurFragment(imageUri.toString())
    }

}