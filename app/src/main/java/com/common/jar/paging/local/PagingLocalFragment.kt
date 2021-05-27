package com.common.jar.paging.local

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.jar.EditTitleLiveData
import com.common.jar.databinding.FragmentLocalPagingBinding
import com.common.tool.base.BaseFragment

/**
 * @author 李雄厚
 * Paging 大致使用 https://developer.android.com/jetpack/androidx/releases/paging
 *
 * 数据加载本地代码for循环生成的数据
 *
 * @features RecyclerView分页加载数据
 *
 */
class PagingLocalFragment : BaseFragment<FragmentLocalPagingBinding, PagingViewModel>() {
    private var adapter = StudentAdapter()

    override fun onBackClickListener() {
        requireActivity().finish()
    }
    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.post("分页加载")

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        model.setData()
        model.convertList?.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

}