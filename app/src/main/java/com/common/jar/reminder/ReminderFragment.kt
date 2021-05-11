package com.common.jar.reminder

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.common.jar.R
import com.common.jar.databinding.FragmentReminderBinding
import com.common.jar.dialog.EditReminderDialog
import com.common.tool.base.BaseFragment
import com.common.tool.base.EmptyViewModel
import com.common.tool.data.exception.ApiError
import com.common.tool.live_data_bus.LiveDataBus
import com.common.tool.notify.Reminder
import com.common.tool.notify.ReminderNotifyManager
import com.common.tool.sp.SharedPreferenceUtils
import com.common.tool.util.DateTimeUtil
import com.common.tool.util.NonDoubleClick.clickWithTrigger
import com.common.tool.util.dpToPx

/**
 * @author 李雄厚
 *
 * @features 时间提醒
 */
class ReminderFragment : BaseFragment<FragmentReminderBinding, EmptyViewModel>() {
    private var reminderZoom = true
    private var testZoom = true
    private var selectedDel = false
    private var reminderId = ""
    private lateinit var adapter: ReminderAdapter
    private lateinit var titleTick: AppCompatImageView
    private val reminders by lazy {
        val list = mutableListOf<Reminder>()
        list.add(Reminder("1001", "06:00", remarks = "空腹"))
        list.add(Reminder("1002", "08:00", remarks = "早餐后"))
        list.add(Reminder("1003", "10:30", remarks = "午餐前"))
        list.add(Reminder("1004", "13:00", remarks = "午餐后"))
        list.add(Reminder("1005", "16:00", remarks = "晚餐前"))
        list.add(Reminder("1006", "19:00", remarks = "晚餐后"))
        list
    }
    private val localReminders by lazy {
        if (SharedPreferenceUtils.getListData("reminder", Reminder::class.java).isNullOrEmpty()) {
            SharedPreferenceUtils.putListData("reminder", reminders)
        }
        SharedPreferenceUtils.getListData("reminder", Reminder::class.java)
    }


    override fun onBackClickListener() {
        if (selectedDel) {
            selectedDel = false
            adapter.data.forEach {
                it.selectedDel = false
                it.selected = false
            }
            titleTick.visibility = View.GONE
            adapter.notifyDataSetChanged()
            setTranslationAnimation(binding.addReminder, true)
            setTranslationAnimation(binding.delReminder, false)
            return
        }
        requireActivity().finish()
    }

    override fun initView(savedInstanceState: Bundle?) {
        initTitleBar("闹钟")
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
        titleTick = getTitleBar().findViewById(R.id.baseTitleTick)
        titleTick.run {
            setImageResource(R.drawable.ic_select_all)
            clickWithTrigger {
                if (isAllSelected()) {
                    adapter.data.forEach {
                        it.selected = false
                    }
                } else {
                    adapter.data.forEach {
                        it.selected = true
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
        binding.fragment = this
        adapter = ReminderAdapter()
        binding.adapter = adapter
        adapter.addAllData(localReminders)

        (requireActivity() as ReminderActivity).touchEvent = {
            dispatchTouchEvent(it)
        }

    }


    override fun initData() {
        //适配器点击事件
        adapter.clickEvent = { data, _, _ ->
            if (selectedDel) {
                adapter.data.forEach {
                    if (it.reminderId == data.reminderId) {
                        it.selected = !it.selected
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                val dialog = EditReminderDialog.instance(data)
                //对话框完成回调
                dialog.clickEvent = {
                    if (it.turnNn) {
                        it.timeLong = ReminderAdapter.setTimeLong(it.time)
                        ReminderNotifyManager.setNotify(it)
                        it.timeLong = -1L
                    } else {
                        ReminderNotifyManager.cancelWork(it.reminderId)
                    }
                    run breaking@{
                        adapter.data.forEachIndexed { index, reminder ->
                            if (reminder.reminderId == it.reminderId) {
                                adapter.data.remove(reminder)
                                adapter.data.add(index, it)
                                adapter.notifyDataSetChanged()
                                return@breaking
                            }
                        }
                    }
                    SharedPreferenceUtils.putListData("reminder", adapter.data)

                }
                //对话框更多设置回调
                dialog.moreSettingsEvent = {
                    reminderId = it.reminderId
                    val bundle = Bundle()
                    bundle.putParcelable("reminder", it)
                    if (findNavController()?.currentDestination?.id == R.id.reminderFragment) {
                        findNavController()?.navigate(R.id.actionAddReminderFragment, bundle)
                    }
                }
                dialog.show(childFragmentManager)
            }
        }
        //适配器长按回调
        adapter.longClickEvent =
            { data, _, _ ->
                if (!data.selectedDel) {
                    selectedDel = true
                    adapter.data.forEach {
                        it.selectedDel = true
                        if (it.reminderId == data.reminderId) {
                            it.selected = true
                        }
                    }
                    titleTick.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                    setTranslationAnimation(binding.addReminder, false)
                    setTranslationAnimation(binding.delReminder, true)
                }
            }
        //适配器开关回调
        adapter.checkedChangeListener =
            { isChecked, reminderId ->
                adapter.data.forEach {
                    if (it.reminderId == reminderId) {
                        it.turnNn = isChecked
                    }
                }
                SharedPreferenceUtils.putListData("reminder", adapter.data)
            }

        //闹钟响应事件
        reminderModel.reminder.observe(this) {
            if (it.rule == "只响一次") {
                run breaking@{
                    adapter.data.forEachIndexed { index, reminder ->
                        if (reminder.reminderId == it.reminderId) {
                            adapter.data.remove(reminder)
                            it.turnNn = false
                            adapter.data.add(index, it)
                            adapter.notifyDataSetChanged()
                            return@breaking
                        }
                    }
                }
            }

        }

        //添加闹钟回调
        LiveDataBus.liveDataBus.with<Reminder>("addReminder").observe(viewLifecycleOwner, Observer {
            it.timeLong = ReminderAdapter.setTimeLong(it.time)
            ReminderNotifyManager.setNotify(it)
            it.timeLong = -1L
            if (reminderId.isEmpty()) {
                adapter.addData(it)
            } else {
                run breaking@{
                    adapter.data.forEachIndexed { index, reminder ->
                        if (reminder.reminderId == it.reminderId) {
                            adapter.data.remove(reminder)
                            adapter.data.add(index, it)
                            adapter.notifyDataSetChanged()
                            return@breaking
                        }
                    }
                }
            }
            SharedPreferenceUtils.putListData("reminder", adapter.data)
        })

        handler.postDelayed(task, (60 - DateTimeUtil.currentTime(DateTimeUtil.SS).toLong()) * 1000)
    }

    private fun isAllSelected(): Boolean {
        var count = 0
        adapter.data.forEach {
            if (it.selected) {
                count++
            }
        }
        return count == adapter.data.size
    }

    private val handler = Handler(Looper.myLooper()!!)
    private val task: Runnable = object : Runnable {
        override fun run() {
            Log.e("测试", "刷新")
            adapter.notifyDataSetChanged()
            handler.postDelayed(this, 60 * 1000.toLong())

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(task)
    }

    fun navigateAddReminderFragment() {
        reminderId = ""
        if (findNavController()?.currentDestination?.id == R.id.reminderFragment) {
            findNavController()?.navigate(R.id.actionAddReminderFragment)
        }
    }

    fun deleteReminder() {
        run breaking@{
            adapter.data.forEachIndexed { index, reminder ->
                if (reminder.selected) {
                    adapter.data.removeAt(index)
                    ReminderNotifyManager.cancelWork(reminder.reminderId)
                    return@breaking
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun dispatchTouchEvent(ev: MotionEvent?) {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (inRangeOfView(binding.addReminder, ev)) {
                    reminderZoom = false
                    setScaleAnimation(binding.addReminder, false)
                }
                if (inRangeOfView(binding.delReminder, ev)) {
                    testZoom = false
                    setScaleAnimation(binding.delReminder, false)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!inRangeOfView(binding.addReminder, ev)) {
                    if (!reminderZoom) {
                        reminderZoom = true
                        setScaleAnimation(binding.addReminder, true)
                    }
                }

                if (!inRangeOfView(binding.delReminder, ev)) {
                    if (!testZoom) {
                        testZoom = true
                        setScaleAnimation(binding.delReminder, true)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (inRangeOfView(binding.addReminder, ev)) {
                    if (!reminderZoom) {
                        reminderZoom = true
                        setScaleAnimation(binding.addReminder, true)
                    }
                }

                if (inRangeOfView(binding.delReminder, ev)) {
                    if (!testZoom) {
                        testZoom = true
                        setScaleAnimation(binding.delReminder, true)
                    }
                }
            }
        }
    }


    /**
     * 触摸点是否在视图范围内
     *
     * @param view 视图
     * @param ev   触摸点
     * @return 是否在视图范围内
     */
    private fun inRangeOfView(view: View, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        return ev.x >= x && ev.x <= x + view.width && ev.y >= y && ev.y <= y + view.height
    }

    /**
     * @param view 视图
     * @param zoom true 放大 false 缩小
     */
    private fun setScaleAnimation(view: View, zoom: Boolean) {

        val animationX = ObjectAnimator.ofFloat(
            view,
            "scaleX",
            if (zoom) 0.9F else 1F, if (zoom) 1F else 0.9F
        )
        val animationY = ObjectAnimator.ofFloat(
            view,
            "scaleY",
            if (zoom) 0.9F else 1F, if (zoom) 1F else 0.9F
        )
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animationX, animationY)
        animatorSet.duration = 200
        animatorSet.start()
    }

    private fun setTranslationAnimation(view: View, action: Boolean) {
        val animation = ObjectAnimator.ofFloat(
            view,
            "translationY",
            if (action) (120.dpToPx).toFloat() else 0F, if (action) 0F else (120.dpToPx).toFloat()
        )
        animation.duration = 500
        animation.start()
    }
}