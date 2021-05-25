package com.common.tool.dialog

import android.view.Gravity
import com.common.tool.R
import com.common.tool.base.BaseFragmentDialog
import com.common.tool.databinding.DialogCommonBinding

/**
 * @author ���ۺ�
 *
 * @features ͨ����ʽ�Ի���
 */
class CommonDialog : BaseFragmentDialog<DialogCommonBinding>() {

    init {
        setOutCancel(false)
        setMargin(50)
        setAnimStyle(R.style.commonStyleDialog)
        setGravity(Gravity.CENTER)
    }

    var content = ""
    var title = ""
    var confirm = "ȷ��"
    var showCancel = true
    var showConfirm = true
    var showTitle = true
    var onConfirm: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    override fun convertView(binding: DialogCommonBinding) {
        binding.data = Data(title, content, confirm, showTitle, showCancel, showConfirm)
        binding.commonDialogContent.setOnClickListener {
            dismiss()
            onCancel?.invoke()
        }
        binding.commonDialogConfirm.setOnClickListener {
            dismiss()
            onConfirm?.invoke()

        }
    }
    data class Data(
        val title: String,
        val content: String,
        val confirm: String,
        val showTitle: Boolean,
        val showCancel: Boolean,
        val showConfirm: Boolean
    )

}