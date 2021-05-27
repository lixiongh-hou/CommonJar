package com.common.jar.paging.server

import android.os.Bundle
import androidx.lifecycle.Observer
import com.common.jar.EditTitleLiveData
import com.common.jar.databinding.FragmentPagingServerBinding
import com.common.tool.base.BaseFragment

/**
 * @author ���ۺ�
 * Paging ����ʹ�� https://developer.android.com/jetpack/androidx/releases/paging
 *
 * ���ݼ��ط��������ص�����
 *
 * @features RecyclerView��ҳ��������
 *
 */
class PagingServerFragment : BaseFragment<FragmentPagingServerBinding, PagingServerViewModel>() {
    override fun onBackClickListener() {
        requireActivity().finish()
    }
    private lateinit var adapter: ArticleAdapter
    override fun initView(savedInstanceState: Bundle?) {
        EditTitleLiveData.post("��ҳ����")
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