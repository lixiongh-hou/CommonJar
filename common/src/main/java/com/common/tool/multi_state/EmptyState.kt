package com.common.tool.multi_state

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.common.tool.R
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer

/**
 * @author 李雄厚
 *
 * @features 自定义空状态
 */
class EmptyState: MultiState() {
    override fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View = inflater.inflate(R.layout.mult_state_empty, container, false)

    override fun onMultiStateViewCreate(view: View) {
    }
}