package com.common.jar.reminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.common.jar.R
import com.common.jar.databinding.FragmentAddReminderBinding
import com.common.tool.base.BaseFragment
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.DateTimeUtil.formatTime
import com.common.tool.util.NonDoubleClick.clickWithTrigger


/**
 * @author 李雄厚
 *
 * @features 设置提醒
 */
class AddReminderFragment : BaseFragment<FragmentAddReminderBinding, AddReminderViewModel>() {
    private var hourValue = 0
    private var houString = ""
    private var minuteValue = 0
    private var minuteString = ""
    private lateinit var titleHint: AppCompatTextView
    companion object{
        val pickerHourList by lazy {
            val list = mutableListOf<String>()
            for (i in 0 until 24) {
                if (i.toString().length <= 1) {
                    list.add("0$i")
                } else {
                    list.add(i.toString())
                }
            }
            list
        }

        val pickerMinuteList by lazy {
            val list = mutableListOf<String>()
            for (i in 0 until 60) {
                if (i.toString().length <= 1) {
                    list.add("0$i")
                } else {
                    list.add(i.toString())
                }
            }
            list
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        initTitleBar("添加闹钟")
        binding.model = model
        getTitleBar().setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )
        getTitleBar().findViewById<AppCompatTextView>(R.id.baseTitleText).run {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        titleHint = getTitleBar().findViewById(R.id.baseTitleHint)
        titleHint.run {
            visibility = View.VISIBLE
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleClose).run {
            imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleTick).run {
            visibility = View.VISIBLE
            imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            clickWithTrigger {
                model.addReminder("$houString:$minuteString")
                onBackClickListener()
            }
        }

        binding.fragment = this

        initPickView()

    }

    @SuppressLint("SetTextI18n")
    private fun initPickView() {
        val timeList = DateTimeUtil.currentTime(DateTimeUtil.HH_MM).split(":")
        for (i in pickerHourList.indices) {
            if (pickerHourList[i] == timeList[0]) {
                hourValue = i
                houString = pickerHourList[i]
            }
        }
        for (i in pickerMinuteList.indices) {
            if (pickerMinuteList[i] == timeList[1]) {
                minuteValue = i
                minuteString = pickerMinuteList[i]
            }
        }
        titleHint.text = "${formatTime(ReminderAdapter.setTimeLong("$houString:$minuteString"))}后响铃"
//        binding.pickerHour.displayedValues = pickerHourList.toTypedArray()
//        binding.pickerHour.minValue = 1
//        binding.pickerHour.maxValue = pickerHourList.size
//        binding.pickerHour.value = 1
        binding.pickerHour.refreshByNewDisplayedValues(pickerHourList.toTypedArray())
        binding.pickerHour.value = hourValue
        binding.pickerHour.setOnValueChangedListener { _, oldVal, newVal ->
            Log.e("测试", "oldVal,$oldVal,newVal,$newVal")
            houString = newVal.toString()
            titleHint.text =
                "${formatTime(ReminderAdapter.setTimeLong("$houString:$minuteString"))}后响铃"

        }

//        binding.pickerMinute.displayedValues = pickerMinuteList.toTypedArray()
//        binding.pickerMinute.minValue = 1
//        binding.pickerMinute.maxValue = pickerMinuteList.size
//        binding.pickerMinute.value = 1
        binding.pickerMinute.refreshByNewDisplayedValues(pickerMinuteList.toTypedArray())
        binding.pickerMinute.value = minuteValue
        binding.pickerMinute.setOnValueChangedListener { _, oldVal, newVal ->
            Log.e("测试", "oldVal,$oldVal,newVal,$newVal")
            minuteString = newVal.toString()
            titleHint.text =
                "${formatTime(ReminderAdapter.setTimeLong("$houString:$minuteString"))}后响铃"

        }
    }

    override fun initData() {
    }
}