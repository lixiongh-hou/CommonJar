package com.common.jar.view.nine_grid

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import java.io.Serializable;

/**
 * @author 李雄厚
 *
 * @features 九宫格视图适配器
 */
abstract class NineGridViewAdapter(val context: Context, val imageInfo: MutableList<ImageInfo>) :
    Serializable {

    /**
     * 如果要实现图片点击的逻辑，重写此方法即可
     *
     * @param context      上下文
     * @param nineGridView 九宫格控件
     * @param index        当前点击图片的的索引
     * @param imageInfo    图片地址的数据集合
     */
    open fun onImageItemClick(
        context: Context,
        nineGridView: NineGridView,
        index: Int,
        imageInfo: MutableList<ImageInfo>
    ) {
    }

    /**
     * 生成ImageView容器的方式，默认使用NineGridImageViewWrapper类，即点击图片后，图片会有蒙板效果
     * 如果需要自定义图片展示效果，重写此方法即可
     *
     * @param context 上下文
     * @return 生成的 ImageView
     */
    open fun generateImageView(context: Context): AppCompatImageView {
        return AppCompatImageView(context)
    }
}