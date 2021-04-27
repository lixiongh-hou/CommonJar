package com.common.jar.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.common.tool.base.BaseViewModel


/**
 * @author 李雄厚
 *
 * @features 分页模型
 */
class PagingViewModel : BaseViewModel() {

    var convertList: LiveData<PagedList<Student>>? = null
    var concertDataSource: DataSource<Int, Student>? = null

    fun setData() {
        val concertFactory = ConcertFactory()
        concertDataSource = concertFactory.create()
//        convertList = LivePagedListBuilder(concertFactory, PagedList.Config.Builder()
//            .setPageSize(20)                         //配置分页加载的数量
//            .setEnablePlaceholders(false)     //配置是否启动PlaceHolders
//            .setInitialLoadSizeHint(20)              //初始化加载的数量


        convertList = LivePagedListBuilder(concertFactory,  20/* 每页加载多少数据 */)
            .build()

    }


}

internal class ConcertFactory : DataSource.Factory<Int, Student>() {
    private val mSourceLiveData = MutableLiveData<ConcertDataSource>()

    override fun create(): DataSource<Int, Student> {
        val concertDataSource = ConcertDataSource()
        mSourceLiveData.postValue(concertDataSource)
        return concertDataSource
    }

}

