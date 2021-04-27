package com.common.jar.paging

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.jar.EditTitleLiveData
import com.common.jar.databinding.FragmentPagingBinding
import com.common.tool.base.BaseFragment

/**
 * @author 李雄厚
 * Paging 大致使用 https://developer.android.com/jetpack/androidx/releases/paging
 *
 * @features RecyclerView分页加载数据
 */
class PagingFragment : BaseFragment<FragmentPagingBinding, PagingViewModel>() {
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