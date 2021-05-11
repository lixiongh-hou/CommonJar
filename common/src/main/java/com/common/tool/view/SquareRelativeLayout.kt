package com.common.tool.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * @author 李雄厚
 *
 * @features 正方形布局
 */
class SquareRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var isWap = false
    fun setWap(isWap: Boolean) {
        this.isWap = isWap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isWap) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }
}