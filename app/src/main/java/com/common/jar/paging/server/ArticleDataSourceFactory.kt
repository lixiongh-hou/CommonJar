package com.common.jar.paging.server

import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ArticleDataSourceFactory(private val coroutineScope: CoroutineScope): DataSource.Factory<Int, DataEntity>() {
    override fun create(): DataSource<Int, DataEntity> {
        return ArticleDataSource(coroutineScope)
    }
}