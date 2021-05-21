package com.common.jar.play_music

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.common.jar.R
import com.common.jar.play_music.data.LyricBean
import kotlin.concurrent.thread
import kotlin.math.abs

/**
 * @author 李雄厚
 *
 * @features 自定义加载歌词
 */
class LyricView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private var bounds: Rect = Rect()

    private val list by lazy { ArrayList<LyricBean>() }

    /**
     * 当前View的宽度
     */
    private var viewW = 0

    /**
     * 当前View的高度
     */
    private var viewH = 0

    /**
     * 文本大号字体
     */
    private var smallSize = 0F

    /**
     * 文本小号字体
     */
    private var bigSize = 0F

    /**
     * 文本白色
     */
    private var white = 0

    /**
     * 文本灰色
     */
    private var gray = 0

    /**
     * 居中行位置
     */
    private var contentLine = 0

    /**
     * 设置行高
     */
    private var lineHeight = 0

    /**
     * 歌曲总时长
     */
    var duration = 0

    /**
     * 歌曲当前进度
     */
    private var progress = 0

    /**
     * 指定是否可用通过progress进度更新歌词
     */
    private var updateByPro = true

    init {
        smallSize = resources.getDimension(R.dimen.smallSize)
        bigSize = resources.getDimension(R.dimen.bigSize)
        white = ContextCompat.getColor(context, R.color.white)
        gray = ContextCompat.getColor(context, R.color.green)
        lineHeight = resources.getDimensionPixelOffset(R.dimen.lineHeight)
        //修改x轴位置居中显示
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (list.size == 0) {
            //歌词没来得及加载集合等于0
            drawSingleLine(canvas)
        } else {
            drawMultiLine(canvas)
        }

    }

    /**
     * 绘制多行居中文本
     */
    private var offsetY = 0F
    private fun drawMultiLine(canvas: Canvas?) {
        if (updateByPro) {
            //求居中行的偏移Y
            /**
             * 行可用时间:
             * 1、最后一行居中:
             * 行可用时间 = duration - 最后一行开始时间
             * 2、其他行居中:
             * 行可用时间 = 下一行开始时间 - 居中行开始时间
             * 偏移时间 = progress - 居中行开始时间
             * 偏移百分比 = 偏移时间 / 行可用时间
             * 偏移Y = 偏移百分比 * 行高
             */
            //行可用时间
            val lineTme = if (contentLine == list.size - 1) {
                //1、最后一行居中:
                //行可用时间 = duration - 最后一行开始时间
                duration - list[contentLine].starTime
            } else {
                //2、其他行居中:
                //行可用时间 = 下一行开始时间 - 居中行开始时间
                val centerS = list[contentLine].starTime
                val nextS = list[contentLine + 1].starTime
                nextS - centerS
            }
            //偏移时间 = progress - 居中行开始时间
            val offsetTime = progress - list[contentLine].starTime
            //偏移百分比 = 偏移时间 / 行可用时间
            val offsetPercent = offsetTime / (lineTme.toFloat())
            //偏移Y = 偏移百分比 * 行高
            offsetY = offsetPercent * lineHeight
        }

        val contentText = list[contentLine].content
        //求文本的高度和宽度
        paint.getTextBounds(contentText, 0, contentText.length, bounds)
        val textH = bounds.height()
        //求出文本居中的X,Y
        val contentX = viewW / 2
        val contentY = viewH / 2 + textH / 2 - offsetY
        for ((index, value) in list.withIndex()) {
            if (index == contentLine) {
                paint.textSize = bigSize
                paint.color = gray
            } else {
                paint.textSize = smallSize
                paint.color = white
            }
            //求出每行文本Y
            val curY = contentY + (index - contentLine) * lineHeight
            //超出上边界不进行绘制
            if (curY < 0) continue
            //超出下边界不进行绘制
            if (curY > viewH + lineHeight) break
            canvas?.drawText(value.content, contentX.toFloat(), curY, paint)
        }
    }

    /**
     * 绘制单行居中文本
     */
    private fun drawSingleLine(canvas: Canvas?) {
        paint.textSize = bigSize
        paint.color = gray
        val text = "正在加载歌词..."
        //求文本的高度和宽度
        paint.getTextBounds(text, 0, text.length, bounds)
//        val textW = bounds.width()
        val textH = bounds.height()
        //求出文本居中的X,Y
//        val x = viewW / 2 - textW / 2
        val x = viewW / 2
        val y = viewH / 2 + textH / 2
        canvas?.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    /**
     * 布局之后执行
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewW = w
        viewH = h
    }

    /**
     * 通过进度更新歌词
     */
    fun updateProgress(progress: Int) {
        if (!updateByPro) {
            return
        }
        if (list.isEmpty()) {
            return
        }
        this.progress = progress
        //获取居中行
        //先判断居中行是否是最后一行
        if (progress >= list[list.size - 1].starTime) {
            //progress >= 最后一行的开始时间，最后一行居中
            contentLine = list.size - 1
        } else {
            //其他行居中，遍历循环
            for (i in 0 until list.size - 1) {
                //progress >= 当前开始时间 && progress < 下一行开始时间
                val curStarTime = list[i].starTime
                val nextStarTime = list[i + 1].starTime
                if (progress in curStarTime until nextStarTime) {
                    contentLine = i
                    break
                }
            }
        }
        //刷新View
        invalidate()
    }

    /**
     * 设置歌词播放名称
     * 解析歌词文件添加到集合中
     */
    fun setSongName(name: String) {
        thread {
            list.clear()
            Log.e("测试", "歌曲名字$name")
            list.addAll(name.loadLyricFile().parseLyric())
        }
    }

    private var downY = 0F
    private var markY = 0F

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    //停止进度更新歌词
                    updateByPro = false
                    //记录手指按下的Y
                    downY = it.y
                    //记录原来进度已经更新Y
                    markY = this.offsetY
                }
                MotionEvent.ACTION_MOVE -> {
                    //当前Y
                    val endY = it.y
                    //求手指移动的Y
                    val offY = downY - endY
                    //重新设置居中偏移
                    this.offsetY = offY + markY
                    //如果最终的Y的偏移大于行高 重新确定居中行
                    if (abs(this.offsetY) >= lineHeight) {
                        //求居中行号偏移
                        val offsetLine = (this.offsetY / lineHeight).toInt()
                        contentLine += offsetLine
                        //对居中行做编辑处理
                        if (contentLine < 0) contentLine = 0 else if (contentLine > list.size - 1) contentLine = list.size - 1
                        //重新设置downY
                        downY = endY
                        //重新确定偏移Y
                        this.offsetY = this.offsetY % lineHeight
                        markY = this.offsetY
                        listener?.invoke(list[contentLine].starTime)
                    }

                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    //开始进度更新歌词
                    updateByPro = true
                }
            }
        }
        return true
    }

    private var listener: ((progress: Int) -> Unit)? = null
    fun setlListener(listener: ((progress: Int) -> Unit)){
        this.listener = listener
    }
}