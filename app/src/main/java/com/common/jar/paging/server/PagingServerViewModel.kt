package com.common.jar.paging.server

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.common.tool.base.BaseViewModel


/**
 * @author 李雄厚
 *
 * @features ***
 */
class PagingServerViewModel : BaseViewModel() {
    lateinit var articlePageList: LiveData<PagedList<DataEntity>>

    fun setData() {
        val concertFactory = ArticleDataSourceFactory(viewModelScope)
        articlePageList = LivePagedListBuilder(
            concertFactory, PagedList.Config.Builder()
                //设置控件占位，是否需要为为显示的数据预留位置
                .setEnablePlaceholders(false)
                //每一页的大小
                .setPageSize(20)
                //距离底部还有多少条数据的是加载下一条
                .setPrefetchDistance(10)
                //首次加载的数量
                .setInitialLoadSizeHint(20)
                .build()
        ).build()

    }

    /**下拉刷新 */
    fun resetQuery() {
        articlePageList.value?.dataSource?.invalidate()
    }


}