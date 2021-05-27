package com.common.jar.paging.server

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.common.tool.base.BaseViewModel


/**
 * @author ���ۺ�
 *
 * @features ***
 */
class PagingServerViewModel : BaseViewModel() {
    lateinit var articlePageList: LiveData<PagedList<DataEntity>>

    fun setData() {
        val concertFactory = ArticleDataSourceFactory(viewModelScope)
        articlePageList = LivePagedListBuilder(
            concertFactory, PagedList.Config.Builder()
                //���ÿؼ�ռλ���Ƿ���ҪΪΪ��ʾ������Ԥ��λ��
                .setEnablePlaceholders(false)
                //ÿһҳ�Ĵ�С
                .setPageSize(20)
                //����ײ����ж��������ݵ��Ǽ�����һ��
                .setPrefetchDistance(10)
                //�״μ��ص�����
                .setInitialLoadSizeHint(20)
                .build()
        ).build()

    }

    /**����ˢ�� */
    fun resetQuery() {
        articlePageList.value?.dataSource?.invalidate()
    }


}