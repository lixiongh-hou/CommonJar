package com.common.jar.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.common.jar.databinding.DialogRuleBinding
import com.common.jar.databinding.ItemRuleBinding
import com.common.tool.R
import com.common.tool.base.BaseFragmentDialog
import com.common.tool.base.rv.BaseAdapter

/**
 * @author 李雄厚
 *
 * @features 规则对话框
 */
class RuleDialog : BaseFragmentDialog<DialogRuleBinding>() {

    var clickEvent: ((String) -> Unit)? = null
    private lateinit var adapter: BaseAdapter<Rule, *>
    private val ruleList by lazy {
        val list = mutableListOf<Rule>()
        list.add(Rule("只响一次", false))
        list.add(Rule("每天", false))
        list.add(Rule("法定工作日(智能跳过节假日)", false))
        list.add(Rule("法定节假日(智能跳过工作日)", false))
        list.add(Rule("周一至周五", false))
        list.add(Rule("自定义", false))
        list
    }

    companion object {
        fun instance(rule: String): RuleDialog =
            RuleDialog().apply {
                setGravity(Gravity.BOTTOM)
                setAnimStyle(R.style.DialogBottomAnim)
                arguments = Bundle().apply {
                    putString("rule", rule)
                }
            }

    }

    override fun convertView(binding: DialogRuleBinding) {
        val rule = arguments?.getString("rule")
        ruleList.forEach {
            if (rule == it.rule) {
                it.selected = true
            }
        }
        adapter = RuleAdapter()
        binding.adapter = adapter
        adapter.refreshData(ruleList)
        adapter.clickEvent = { data, _, _ ->
            adapter.data.forEach { rule ->
                rule.selected = false
            }
            data.selected = !data.selected
            adapter.notifyDataSetChanged()
            clickEvent?.invoke(data.rule)
            dismiss()
        }
    }

    inner class RuleAdapter : BaseAdapter<Rule, ItemRuleBinding>() {
        override fun createBinding(parent: ViewGroup, viewType: Int): ItemRuleBinding =
            ItemRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        override fun bind(binding: ItemRuleBinding, data: Rule, position: Int) {
            binding.rule = data.rule
            binding.selected = data.selected
            binding.rLinearLayout.isSelected = data.selected
        }
    }

    data class Rule(val rule: String, var selected: Boolean)
}