package com.common.jar.work

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * @author 李雄厚
 *
 * @features Work工具类
 */


/**
 * 模糊图片
 */
@WorkerThread
fun blurBitmap(bitmap :Bitmap, applicationContext: Context): Bitmap{
    lateinit var rsContext: RenderScript
    try {

        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height, bitmap.config)

        rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
        val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
        val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
        val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        theIntrinsic.apply {
            setRadius(10f)
            theIntrinsic.setInput(inAlloc)
            theIntrinsic.forEach(outAlloc)
        }
        outAlloc.copyTo(output)
        return output
    }finally {
        rsContext.finish()
    }
}

/**
 * 把 Bitmap写入本地文件并返回 Uri
 * @param applicationContext Application context
 * @param bitmap Bitmap to write to temp file
 * @return Uri for temp file with bitmap
 * @throws FileNotFoundException Throws if bitmap file cannot be found
 */
@Throws(FileNotFoundException::class)
fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs() // should succeed
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore: IOException) {
            }

        }
    }
    return Uri.fromFile(outputFile)
}

/**
 * 固定时间睡眠以模仿较慢工作的方法
 */
fun sleep() {
    try {
        Thread.sleep(DELAY_TIME_MILLIS, 0)
    } catch (e: InterruptedException) {
        Log.e("测试", e.message.toString())
    }

}