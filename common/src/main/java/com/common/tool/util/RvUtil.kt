package com.common.tool.util

import androidx.recyclerview.widget.RecyclerView

/**
 * @author 李雄厚
 *
 * @features ***
 */
fun RecyclerView.solveNestQuestion() {
    // 解决数据加载不全的问题
    isNestedScrollingEnabled = false
    setHasFixedSize(true)
    // 解决数据加载完成后，没有停留在顶部的问题
    isFocusable = false
}