package com.common.jar.work

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * @author 李雄厚
 *
 * @features 将图片模糊处理
 */
class BlurWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        private val TAG = BlurWorker::class.java.simpleName
    }

    override fun doWork(): Result {

        val appContext = applicationContext


        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        (0..100 step 10).forEach {
            setProgressAsync(workDataOf(PROGRESS to it))
            Log.e(TAG, it.toString())
            sleep()
        }

        return try {
            if (resourceUri.isNullOrEmpty()){
                Log.e(TAG, "无效的输入uri")
                throw IllegalArgumentException("无效的输入uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(picture, appContext)
            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, "应用模糊时出错")
            Result.failure()
        }
    }
}