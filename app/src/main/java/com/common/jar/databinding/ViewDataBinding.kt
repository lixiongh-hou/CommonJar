package com.common.jar.databinding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.common.tool.base.BaseApp

/**
 * @author 李雄厚
 *
 * @features 数据绑定
 */
object ViewDataBinding {

    @JvmStatic
    @BindingAdapter(value = ["imageUri"])
    fun setImageUri(imageView: ImageView, uri: Uri?) {
        if (uri == null) {
            return
        }
        imageView.load(uri) {
            crossfade(true)
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
    @BindingAdapter(value = ["rvGAdapter", "size"])
    fun setRvLAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?, size: Int) {
        if (adapter == null) {
            return
        }
        recyclerView.layoutManager = GridLayoutManager(BaseApp.instance, size)
        recyclerView.adapter = adapter
    }
}