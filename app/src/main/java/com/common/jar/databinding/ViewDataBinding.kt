package com.common.jar.databinding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.loadAny
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features 数据绑定
 */
object ViewDataBinding {

    @JvmStatic
    @BindingAdapter(value = ["imageUri", "isUri", "isHttp", "circle", "roundedCorners"], requireAll = false)
    fun setImageUri(
        imageView: ImageView,
        uri: String?,
        isUri: Boolean = false,
        isHttp: Boolean = false,
        circle: Boolean = false,
        roundedCorners: Boolean = false
    ) {
        if (uri == null) {
            return
        }

        imageView.loadAny(
            when {
                isUri -> {
                    Uri.parse(uri)
                }
                isHttp -> {
                    uri
                }
                else -> {
                    uri
                }
            }
        ) {
            crossfade(true)
            if (circle) {
                transformations(CircleCropTransformation())
            }
            if (roundedCorners) {
                transformations(RoundedCornersTransformation(8F))
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["rvLAdapter"])
    fun setRvLAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        if (adapter == null) {
            return
        }
        recyclerView.layoutManager = LinearLayoutManager(BaseApp.instance)
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter(value = ["rvGAdapter", "size"], requireAll = false)
    fun setRvGAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?, size: Int = 0) {
        if (adapter == null) {
            return
        }
        if (size >= 1) {
            recyclerView.layoutManager = GridLayoutManager(BaseApp.instance, size)
        }
        recyclerView.adapter = adapter
    }
}