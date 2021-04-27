package com.common.tool.base.rv

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * @author 李雄厚
 *
 * @features RecyclerView.ViewHolder基类
 */
class BaseViewHolder<out Binding: ViewDataBinding>(val binding: Binding): RecyclerView.ViewHolder(binding.root)
