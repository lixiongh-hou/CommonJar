package com.common.jar.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

/**
 * @author 李雄厚
 *
 * @features 清除缓存
 */
class CleanupWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    companion object {
        private val TAG = CleanupWorker::class.java.simpleName
    }

    override fun doWork(): Result {

        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.e(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())
            Result.failure()
        }
    }

}
