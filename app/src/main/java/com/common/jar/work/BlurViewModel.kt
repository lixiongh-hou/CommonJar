package com.common.jar.work

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.work.*
import com.common.tool.base.BaseApp
import com.common.tool.base.BaseViewModel

/**
 * @author 李雄厚
 *
 * @features 模糊图片页面模型
 */
class BlurViewModel : BaseViewModel() {
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    internal var outputString: String? = null
    private val workManager = WorkManager.getInstance(BaseApp.instance)
    internal val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    internal val progressWorkInfoItems: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_PROGRESS)

    internal fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    internal fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanupWorker::class.java)
        )

        // Add WorkRequests to blur the image the number of times requested
        for (i in 0 until blurLevel) {

            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }

            blurBuilder.addTag(TAG_PROGRESS)
            continuation = continuation.then(blurBuilder.build())
        }

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        // Add WorkRequest to save the image to the filesystem
        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)

        // Actually start the work
        continuation.enqueue()
    }

    /**
     * 创建包含要操作的Uri的输入数据包
     * @return 包含图像Uri作为字符串的数据
     */
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, it.toString())
        }
        return builder.build()
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    internal fun setImageUri(rui: String) {
        imageUri = uriOrNull(rui)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
        outputString = outputImageUri
    }
}