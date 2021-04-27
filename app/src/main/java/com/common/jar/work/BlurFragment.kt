package com.common.jar.work

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.common.jar.EditTitleLiveData
import com.common.jar.R
import com.common.jar.databinding.FragmentBlurBinding
import com.common.tool.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_blur.view.*

/**
 * @author 李雄厚
 * WorkManager 大致使用 https://developer.android.google.cn/topic/libraries/architecture/workmanager
 *
 * @features 模糊图片页面
 */
class BlurFragment : BaseFragment<FragmentBlurBinding, BlurViewModel>() {
    override fun onBackClickListener() {
        super.onBackClickListener()
        EditTitleLiveData.post("选择图片")
    }
    @SuppressLint("QueryPermissionsNeeded")
    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.post("模糊图片")
        model.setImageUri(BlurFragmentArgs.fromBundle(requireArguments()).uri)
        binding.uri = model.imageUri

        binding.goButton.setOnClickListener { model.applyBlur(blurLevel) }

        binding.cancelButton.setOnClickListener { model.cancelWork() }

        // Setup view output image file button
        binding.seeFileButton.setOnClickListener {
            binding.blurImage.visibility = View.VISIBLE
            binding.blurUri = model.outputUri
        }
    }

    override fun initData() {

        model.outputWorkInfos.observe(viewLifecycleOwner, workInfoObserver())

        // 显示 Work 进度
        model.progressWorkInfoItems.observe(viewLifecycleOwner, progressObserver())
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = it[0]
            if (workInfo.state.isFinished) {
                showWorkFinished()

                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                if (!outputImageUri.isNullOrEmpty()) {
                    model.setOutputUri(outputImageUri as String)
                    binding.seeFileButton.visibility = View.VISIBLE
                }

            } else {
                showWorkInProgress()
            }
        }
    }

    private fun progressObserver(): Observer<List<WorkInfo>> {
        return Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            it.forEach { workInfo ->
                if (WorkInfo.State.RUNNING == workInfo.state) {
                    val progress = workInfo.progress.getInt(PROGRESS, 0)
                    binding.progressBar.progress = progress
                }
            }
        }
    }

    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
            progressBar.progress = 0
        }
    }

    private val blurLevel
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radioBlurLv1 -> 1
                R.id.radioBlurLv2 -> 2
                R.id.radioBlurLv3 -> 3
                else -> 1
            }
}