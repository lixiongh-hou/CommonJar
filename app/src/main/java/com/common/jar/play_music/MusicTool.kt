package com.common.jar.play_music

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import com.common.jar.R
import com.common.jar.play_music.data.LyricBean
import com.google.gson.Gson
import java.io.File
import java.nio.charset.Charset


/**
 * @author 李雄厚
 *
 * @features ***
 */
fun String.filterName(): String {
    return if (this.contains(".")) {
        val split = this.split(".")
        split[0]
    } else {
        this
    }
}

const val HOUR = 60 * 60 * 1000
const val MIN = 60 * 1000
const val SEC = 1000
fun Int.formatTime(): String {
    val hour = this / HOUR
    val min = this % HOUR / MIN
    val sec = this % MIN / SEC
    return if (hour == 0) {
        //不足一小时 不显示小时
        String.format("%02d:%02d", min, sec)
    } else {
        String.format("%02d:%02d%02d", hour, min, sec)
    }
}

/**
 * 根据文件路径
 */
fun String.getAlbumArt(context: Context): Bitmap? {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(this)
    val data = mmr.embeddedPicture
    var albumPicture: Bitmap?
    if (data != null) {
        //获取bitmap对象
        albumPicture = BitmapFactory.decodeByteArray(data, 0, this.length)
        Log.e("测试", Gson().toJson(albumPicture))
        //获取宽高
        val width = albumPicture?.width?:500
        val height = albumPicture?.height?:500
//        Log.e("测试", "width = $width height = $height")
        // 创建操作图片用的Matrix对象
        val matrix = Matrix()
        // 计算缩放比例
        val sx = 120.toFloat() / width
        val sy = 120.toFloat() / height
        // 设置缩放比例
        matrix.postScale(sx, sy)
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
        return albumPicture
    } else {
        albumPicture = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        //ic_launcher是从歌曲文件读取不出来专辑图片时用来代替的默认专辑图片
        val width = albumPicture.width
        val height = albumPicture.height
//        Log.e("测试", "width = $width height = $height")
        // 创建操作图片用的Matrix对象
        val matrix = Matrix()
        // 计算缩放比例
        val sx = 120.toFloat() / width
        val sy = 120.toFloat() / height
        // 设置缩放比例
        matrix.postScale(sx, sy)
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
        return albumPicture
    }
}

private val dir = File(Environment.getExternalStorageDirectory(), "qqmusic/lrc")

fun String.loadLyricFile(): File = File(dir, "${this}.lrc")

fun File.parseLyric(): List<LyricBean> {
    val list = arrayListOf<LyricBean>()
    if (!this.exists()) {
        Log.e("测试", "文件为空")
        list.add(LyricBean(0, "歌词加载错误"))
        return list
    } else {
        val lineList = this.readLines(Charset.forName("gbk")) //读取歌曲文件，返回数据每行集合
        for (line in lineList) {
            //解析一行添加到集合中
            Log.e("测试", "得到歌词${line}")
            list.addAll(line.parseLine())

        }
        //歌词排序
//        list.sortBy { it.starTime }
        //返回集合
        return list
    }
}

/**
 * 解析一行
 * [00:02.85]词：许嵩
 */
private fun String.parseLine(): List<LyricBean> {
    //创建集合
    val list = arrayListOf<LyricBean>()
    //解析当前行
    val arr = this.split("]")
    //获取歌词内容
    val content = arr[arr.size - 1]
    for (index in 0 until arr.size - 1) {
        if (content.isNotEmpty()) {
            val startTime = arr[index].parseTime()
            list.add(LyricBean(startTime, content))
        }
    }
    //返回集合
    return list
}

/**
 * 解析时间
 */
private fun String.parseTime(): Int {
    //[去掉
    val timeS = this.substring(1)
    //按照:切割
    val list = timeS.split(":")
    var hour = 0
    val min: Int
    val sec: Float
    if (list.size == 3) {
        //小时
        hour = (list[0].toInt()) * 60 * 60 * 1000
        min = (list[1].toInt()) * 60 * 1000
        sec = (list[2].toFloat()) * 1000
    } else {
        //没有小时
        min = (list[0].toInt()) * 60 * 1000
        sec = (list[1].toFloat()) * 1000
    }
    return (hour + min + sec).toInt()
}
