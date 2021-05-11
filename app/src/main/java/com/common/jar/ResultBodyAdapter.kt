package com.common.jar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.common.jar.databinding.ItemResultBodyBinding
import com.common.jar.view.nine_grid.ImageInfo
import com.common.jar.view.nine_grid.NineGridView
import com.common.jar.view.nine_grid.NineGridViewAdapter
import com.common.tool.base.rv.BaseAdapter

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ResultBodyAdapter : BaseAdapter<VoResultBody, ItemResultBodyBinding>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemResultBodyBinding =
        ItemResultBodyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bind(binding: ItemResultBodyBinding, data: VoResultBody, position: Int) {
        binding.data = data
//        //动态计算列数
//        val size: Int = data.pic.size
//        val spanCount = if (size == 2 || size == 4) {
//            2
//        } else if (size == 1) {
//            1
//        } else {
//            3
//        }

//        binding.recyclerView.layoutManager = GridLayoutManager(binding.root.context, spanCount)
//        binding.recyclerView.itemAnimator = null
//        val listAdapter = ImageAdapter()
//
//        binding.recyclerView.apply {
//            setHasFixedSize(true)
//            layoutManager = GridLayoutManager(binding.root.context, spanCount)
//            adapter = listAdapter
//        }
//        listAdapter.submitList(data.pic)
//

//        binding.recyclerView.layoutManager = GridLayoutManager(binding.root.context, spanCount)
//        binding.adapter = ImageAdapter().apply {
//            refreshData(data.pic)
//        }
        val imageInfo: MutableList<ImageInfo> = ArrayList()
        data.pic.forEach {
            imageInfo.add(ImageInfo(it.pic, it.pic, it.height, it.width))
        }
        if (data.pic.size == 1) {
            binding.nineGridView.setSingleImageRatio(data.pic[0].width * 1.0f / data.pic[0].height)
        }
        binding.nineGridView.setAdapter(object : NineGridViewAdapter(binding.root.context, imageInfo){
            override fun onImageItemClick(
                context: Context,
                nineGridView: NineGridView,
                index: Int,
                imageInfo: MutableList<ImageInfo>
            ) {
                Log.e("测试", "点击")
            }
        })




    }
}