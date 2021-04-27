package com.common.jar.work

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * @author 李雄厚
 *
 * @features ***
 */
class NestedScrollViewCustomer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {


    override fun getTopFadingEdgeStrength(): Float {
        return 0F
    }
}