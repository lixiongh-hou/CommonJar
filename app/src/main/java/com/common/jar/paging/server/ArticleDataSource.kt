package com.common.jar.paging.server

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import com.common.tool.util.ToastUtil.toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ArticleDataSource(private val coroutineScope: CoroutineScope) :
    PageKeyedDataSource<Int, DataEntity>() {

    var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("测试", "Throws an exception with message: ${throwable.message}")
    }
    private var page = 0

    /**
     * 加载第一页时候回调
     */
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DataEntity>
    ) {
        coroutineScope.launch(exceptionHandler) {
            RemoteRequest.getArticle(page, {
                callback.onResult(it.Articles, null, page + 1)
            }, {
                Log.e("测试", "loadInitial接口请求失败${it.message}---params.requestedLoadSize${params.requestedLoadSize}")
            })
        }
        Log.e("测试", "loadInitial${page}")
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DataEntity>) {

    }

    /**
     * 下一页加载回调
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DataEntity>) {
        coroutineScope.launch(exceptionHandler) {
            RemoteRequest.getArticle(params.key, {
                val nextKey = if (!it.over) params.key + 1 else null
                callback.onResult(it.Articles, nextKey)
            }, {
                Log.e("测试", "loadAfter接口请求失败${it.message}")
            })
        }
        Log.e("测试", "loadAfter${params.key}---params.requestedLoadSize${params.requestedLoadSize}")
    }
}