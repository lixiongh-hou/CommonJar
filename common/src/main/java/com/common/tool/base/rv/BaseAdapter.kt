package com.common.tool.base.rv

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.common.tool.util.NonDoubleClick.clickWithTrigger

/**
 * @author 李雄厚
 *
 * @features RecyclerView适配器基类
 */
abstract class BaseAdapter<T, Binding : ViewDataBinding> :
    RecyclerView.Adapter<BaseViewHolder<Binding>>() {

    var data: ArrayList<T> = ArrayList()
    var clickEvent: ((T, Binding, Int) -> Unit)? = null
    var longClickEvent: ((T, Binding, Int) -> Unit)? = null

    fun refreshData(newData: List<T>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addData(t: T) {
        data.add(t)
        notifyDataSetChanged()
    }

    fun addAllData(list: List<T>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Binding> =
        BaseViewHolder(createBinding(parent, viewType))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder<Binding>, position: Int) {
        bind(holder.binding, data[position], position)
        holder.binding.executePendingBindings()
        holder.binding.root.clickWithTrigger {
            clickEvent?.invoke(data[position], holder.binding, position)
        }
        holder.binding.root.setOnLongClickListener {
            longClickEvent?.invoke(data[position], holder.binding, position)
            false
        }
        holder.setIsRecyclable(false)
    }

    abstract fun createBinding(parent: ViewGroup, viewType: Int): Binding

    abstract fun bind(binding: Binding, data: T, position: Int)
}