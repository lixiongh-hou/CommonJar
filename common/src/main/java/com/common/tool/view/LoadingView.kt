package com.common.tool.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.common.tool.util.dpToPx

/**
 * @author 李雄厚
 *
 * @features 进度View
 */
class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint1 = Paint()
    private val mPaint2 = Paint()
    private val circleBgPaint = Paint()
    private var percent = 0.083
    private var interval = 0F
    private var radius = 0F

    init {
        if (null == attrs) {
            if (layoutParams !is FrameLayout.LayoutParams) {
                val ps = FrameLayout.LayoutParams(
                    50.dpToPx,
                    50.dpToPx,
                    Gravity.CENTER
                )
                layoutParams = ps
            }
        }
        mPaint1.isAntiAlias = true
        mPaint1.color = Color.WHITE
        mPaint2.isAntiAlias = true
        mPaint2.style = Paint.Style.STROKE
        mPaint2.color = Color.WHITE
        circleBgPaint.isAntiAlias = true
        circleBgPaint.style = Paint.Style.FILL
        circleBgPaint.color = Color.parseColor("#80000000")
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        radius = (if (width >= height) height else width) * 0.8f
        interval = (radius * 0.2).toFloat()
        mPaint2.strokeWidth = interval / 6
        canvas?.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius / 2 - interval / 3,
            circleBgPaint
        )
        canvas?.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius / 2 - interval / 3,
            mPaint2
        )
        val localRect = RectF(
            width / 2 - radius / 2 + interval,
            height / 2 - radius / 2 + interval,
            width / 2 + radius / 2 - interval,
            height / 2 + radius / 2 - interval
        )
        val f1 = (percent * 360).toFloat()
        canvas!!.drawArc(localRect, -90f, f1, true, mPaint1)
    }

    fun setProgress(progress: Double) {
        var pos = progress / 100F
        if (pos == 0.0) {
            pos = 0.083
        }
        visibility = VISIBLE
        percent = pos
        //重新执行onDraw方法,重新绘制图形
        invalidate()
    }

    fun loadCompleted() {
        visibility = GONE
    }

    fun loadFailed() {
        setProgress(1.0)
        visibility = GONE
    }

    fun setOutsideCircleColor(color: Int) {
        mPaint2.color = color
    }

    fun setInsideCircleColor(color: Int) {
        mPaint1.color = color
    }

    fun setTargetView(target: View?) {
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        if (target == null) {
            return
        }
        if (target.parent is FrameLayout) {
            (target.parent as FrameLayout).addView(this)
        } else if (target.parent is ViewGroup){
            val parentContainer = target.parent as ViewGroup
            val groupIndex = parentContainer.indexOfChild(target)
            parentContainer.removeView(target)

            val badgeContainer = FrameLayout(context)
            val parentLayoutParams = target.layoutParams

            badgeContainer.layoutParams = parentLayoutParams
            target.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams)
            badgeContainer.addView(target)

            badgeContainer.addView(this)
        }
    }
}