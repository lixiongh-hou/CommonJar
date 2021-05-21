package com.common.jar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cc.shinichi.library.ImagePreview
import cc.shinichi.library.view.listener.OnOriginProgressListener
import com.common.jar.databinding.ItemResultBodyBinding
import com.common.jar.view.nine_grid.ImageInfo
import com.common.jar.view.nine_grid.NineGridView
import com.common.jar.view.nine_grid.NineGridViewAdapter
import com.common.tool.base.rv.BaseAdapter
import com.common.tool.view.LoadingView
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.CircleIndexIndicator
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.InstanceLoader

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ResultBodyAdapter(context: Context) : BaseAdapter<VoResultBody, ItemResultBodyBinding>() {
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
        binding.nineGridView.setAdapter(object :
            NineGridViewAdapter(binding.root.context, imageInfo) {
            override fun onImageItemClick(
                context: Context,
                nineGridView: NineGridView,
                index: Int,
                imageInfo: MutableList<ImageInfo>
            ) {
                val list: MutableList<cc.shinichi.library.bean.ImageInfo> = ArrayList()
                var imageInfoList: cc.shinichi.library.bean.ImageInfo
                imageInfo.forEach {
                    imageInfoList = cc.shinichi.library.bean.ImageInfo()
                    imageInfoList.originUrl = it.bigImageUrl
                    imageInfoList.thumbnailUrl = it.thumbnailUrl
                    list.add(imageInfoList)
                }
//                Mojito.with(binding.root.context)
//                    .urls(list)
//                    .position(index)
//                    .autoLoadTarget(false)
//                    .setProgressLoader(object : InstanceLoader<IProgress> {
//                        override fun providerInstance(): IProgress {
//                            return DefaultPercentProgress()
//                        }
//                    })
//                    .setIndicator(CircleIndexIndicator())
//                    .start()
                ImagePreview.getInstance()
                    // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                    .setContext(context)
                    // 设置从第几张开始看（索引从0开始）
                    .setIndex(index)
                    // 1：第一步生成的imageInfo List
                    .setImageInfoList(list)
                    //设置是否开启下拉图片退出
                    .setEnableDragClose(true)
                    //设置是否显示下载按钮
                    .setShowDownButton(false)
                    .setProgressLayoutId(R.layout.default_progress_layout, object :
                        OnOriginProgressListener {
                        override fun progress(parentView: View?, progress: Int) {
                            val loadingView = parentView?.findViewById<LoadingView>(R.id.loadingView)
                            loadingView?.setProgress(progress.toDouble())
                        }

                        override fun finish(parentView: View?) {
                            val loadingView = parentView?.findViewById<LoadingView>(R.id.loadingView)
                            loadingView?.loadCompleted()
                        }
                    })
                    // 开启预览
                    .start()
            }
        })


    }
}