package com.common.jar.paging.server

import android.os.Bundle
import androidx.lifecycle.Observer
import com.common.jar.EditTitleLiveData
import com.common.jar.databinding.FragmentPagingServerBinding
import com.common.tool.base.BaseFragment

/**
 * @author 李雄厚
 * Paging 大致使用 https://developer.android.com/jetpack/androidx/releases/paging
 *
 * 数据加载服务器返回的数据
 *
 * @features RecyclerView分页加载数据
 *
 */
class PagingServerFragment : BaseFragment<FragmentPagingServerBinding, PagingServerViewModel>() {
    override fun onBackClickListener() {
        requireActivity().finish()
    }
    private lateinit var adapter: ArticleAdapter
    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.post("分页加载")
        adapter = ArticleAdapter()
        binding.adapter = adapter

    }

    override fun initData() {
        model.setData()
        model.articlePageList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)

            binding.swipeRefreshLayout.isRefreshing = false
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            model.resetQuery()

        }
    }
}