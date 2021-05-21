package com.common.jar.reminder

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import com.common.jar.R
import com.common.jar.databinding.FragmentRemarkBinding
import com.common.tool.base.BaseFragment
import com.common.tool.base.EmptyViewModel
import com.common.tool.bus.LiveDataBus
import com.common.tool.util.NonDoubleClick.clickWithTrigger

/**
 * @author 李雄厚
 *
 * @features 设置备注
 */
class RemarkFragment : BaseFragment<FragmentRemarkBinding, EmptyViewModel>() {
    val addRemarks = ObservableField<String>()
    override fun initView(savedInstanceState: Bundle?) {
        binding.fragment = this
        addRemarks.set(RemarkFragmentArgs.fromBundle(requireArguments()).remark)
        initTitleBar("新建闹钟备注")
        getTitleBar().setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )
        getTitleBar().findViewById<AppCompatTextView>(R.id.baseTitleText).run {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleClose).run {
            imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleTick).run {
            visibility = View.VISIBLE
            imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            clickWithTrigger {
                LiveDataBus.liveDataBus.with<String>("addRemarks").setValue(addRemarks.get() ?: "")
                onBackClickListener()
            }
        }
    }


    override fun initData() {
    }
}