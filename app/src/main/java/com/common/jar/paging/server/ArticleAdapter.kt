package com.common.jar.paging.server

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.common.jar.databinding.ItemArticleBinding

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ArticleAdapter : PagedListAdapter<DataEntity, ArticleViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DataEntity>() {
            /**
             * 元素是否相等
             */
            override fun areItemsTheSame(oldItem: DataEntity, newItem: DataEntity): Boolean =
                oldItem.id == newItem.id

            /**
             * 对象是否相等
             */
            override fun areContentsTheSame(oldItem: DataEntity, newItem: DataEntity): Boolean =
                oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder =
        ArticleViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.binding.data = getItem(position)
    }

}

class ArticleViewHolder(var binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)