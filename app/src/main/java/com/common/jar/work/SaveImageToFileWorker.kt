package com.common.jar.work

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 李雄厚
 *
 * @features 将图片保存到本地
 */
class SaveImageToFileWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

//    private val Title = "Blurred Image"
//    private val dateFormatter = SimpleDateFormat(
//        "yyyy.MM.dd 'at' HH:mm:ss z",
//        Locale.getDefault()
//    )

    companion object {
        private val TAG = SaveImageToFileWorker::class.java.simpleName
    }

    override fun doWork(): Result {
        sleep()

//        val resolver = applicationContext.contentResolver

        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)

//            val bitmap = BitmapFactory.decodeStream(
//                resolver.openInputStream(Uri.parse(resourceUri)))
//            val imageUrl = MediaStore.Images.Media.insertImage(
//                resolver, bitmap, Title, dateFormatter.format(Date()))

            if (!resourceUri.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to resourceUri)
                Result.success(output)
            } else {
                Log.e(TAG, "模糊图片获取失败")
                Result.failure()
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())
            Result.failure()
        }
    }
}