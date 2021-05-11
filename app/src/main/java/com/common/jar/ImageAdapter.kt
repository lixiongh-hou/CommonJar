package com.common.jar

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import coil.load
import coil.transform.RoundedCornersTransformation
import com.common.jar.databinding.ItemImageBinding
import com.common.tool.base.rv.BaseAdapter

/**
 * @author 李雄厚
 *
 * @features 九宫格图片适配器
 */
//class ImageAdapter : ListAdapter<Pic, ViewHolder>(Callback.asConfig()) {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//        ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val data = getItem(position)
//        if (itemCount == 1) {
//            holder.binding.squareRelativeLayout.setWap(true)
//            holder.binding.imageView.apply {
//                updateLayoutParams {
//                    if (data.width > data.height) {
//                        width = DEFAULT_MAX_SIZE
//                        height = DEFAULT_MAX_SIZE * data.height / data.width
//                    } else {
//                        width = DEFAULT_MAX_SIZE * data.width / data.height
//                        height = DEFAULT_MAX_SIZE
//                    }
//                }
//
//                load(data.pic) {
//                    placeholder(ColorDrawable(randomColor()))
//                    parameters(data.parameters)
//                }
//            }
//        } else {
//            holder.binding.squareRelativeLayout.setWap(false)
//        }
//        holder.binding.image = data
//    }
//
//    class ViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
//
//    private object Callback : DiffUtil.ItemCallback<Pic>() {
//        override fun areItemsTheSame(old: Pic, new: Pic) = old.pic == new.pic
//        override fun areContentsTheSame(old: Pic, new: Pic) = old == new
//    }
//
//    companion object {
//        private const val DEFAULT_MAX_SIZE = 660
//    }
//}
class ImageAdapter : BaseAdapter<Pic, ItemImageBinding>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemImageBinding {
        return ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    }

    override fun bind(binding: ItemImageBinding, data: Pic, position: Int) {
        if (itemCount == 1) {
            binding.squareRelativeLayout.setWap(true)
            binding.imageView.updateLayoutParams {
                if (data.width > data.height) {
                    width = DEFAULT_MAX_SIZE
                    height = DEFAULT_MAX_SIZE * data.height / data.width
                } else {
                    width = DEFAULT_MAX_SIZE * data.width / data.height
                    height = DEFAULT_MAX_SIZE
                }
            }
        } else {
            binding.squareRelativeLayout.setWap(false)
        }
        binding.imageView.load(data.pic) {
            placeholder(ColorDrawable(randomColor()))
            transformations(RoundedCornersTransformation(8F))
            parameters(data.parameters)
        }
    }

    companion object {
        private const val DEFAULT_MAX_SIZE = 660
    }
}