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
 * @author ���ۺ�
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
        //����һСʱ ����ʾСʱ
        String.format("%02d:%02d", min, sec)
    } else {
        String.format("%02d:%02d%02d", hour, min, sec)
    }
}

/**
 * �����ļ�·��
 */
fun String.getAlbumArt(context: Context): Bitmap? {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(this)
    val data = mmr.embeddedPicture
    var albumPicture: Bitmap?
    if (data != null) {
        //��ȡbitmap����
        albumPicture = BitmapFactory.decodeByteArray(data, 0, this.length)
        Log.e("����", Gson().toJson(albumPicture))
        //��ȡ���
        val width = albumPicture?.width?:500
        val height = albumPicture?.height?:500
//        Log.e("����", "width = $width height = $height")
        // ��������ͼƬ�õ�Matrix����
        val matrix = Matrix()
        // �������ű���
        val sx = 120.toFloat() / width
        val sy = 120.toFloat() / height
        // �������ű���
        matrix.postScale(sx, sy)
        // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
        return albumPicture
    } else {
        albumPicture = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        //ic_launcher�ǴӸ����ļ���ȡ������ר��ͼƬʱ���������Ĭ��ר��ͼƬ
        val width = albumPicture.width
        val height = albumPicture.height
//        Log.e("����", "width = $width height = $height")
        // ��������ͼƬ�õ�Matrix����
        val matrix = Matrix()
        // �������ű���
        val sx = 120.toFloat() / width
        val sy = 120.toFloat() / height
        // �������ű���
        matrix.postScale(sx, sy)
        // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
        return albumPicture
    }
}

private val dir = File(Environment.getExternalStorageDirectory(), "qqmusic/lrc")

fun String.loadLyricFile(): File = File(dir, "${this}.lrc")

fun File.parseLyric(): List<LyricBean> {
    val list = arrayListOf<LyricBean>()
    if (!this.exists()) {
        Log.e("����", "�ļ�Ϊ��")
        list.add(LyricBean(0, "��ʼ��ش���"))
        return list
    } else {
        val lineList = this.readLines(Charset.forName("gbk")) //��ȡ�����ļ�����������ÿ�м���
        for (line in lineList) {
            //����һ����ӵ�������
            Log.e("����", "�õ����${line}")
            list.addAll(line.parseLine())

        }
        //�������
//        list.sortBy { it.starTime }
        //���ؼ���
        return list
    }
}

/**
 * ����һ��
 * [00:02.85]�ʣ�����
 */
private fun String.parseLine(): List<LyricBean> {
    //��������
    val list = arrayListOf<LyricBean>()
    //������ǰ��
    val arr = this.split("]")
    //��ȡ�������
    val content = arr[arr.size - 1]
    for (index in 0 until arr.size - 1) {
        if (content.isNotEmpty()) {
            val startTime = arr[index].parseTime()
            list.add(LyricBean(startTime, content))
        }
    }
    //���ؼ���
    return list
}

/**
 * ����ʱ��
 */
private fun String.parseTime(): Int {
    //[ȥ��
    val timeS = this.substring(1)
    //����:�и�
    val list = timeS.split(":")
    var hour = 0
    val min: Int
    val sec: Float
    if (list.size == 3) {
        //Сʱ
        hour = (list[0].toInt()) * 60 * 60 * 1000
        min = (list[1].toInt()) * 60 * 1000
        sec = (list[2].toFloat()) * 1000
    } else {
        //û��Сʱ
        min = (list[0].toInt()) * 60 * 1000
        sec = (list[1].toFloat()) * 1000
    }
    return (hour + min + sec).toInt()
}
