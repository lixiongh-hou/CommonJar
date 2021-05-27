package com.common.jar.paging.local

import android.util.Log
import androidx.paging.PositionalDataSource

/**
 * @author 李雄厚
 *
 * @features ***
 */
class ConcertDataSource : PositionalDataSource<Student>() {
    /**
     * 当有了初始化数据之后，滑动的时候如果需要加载数据的话，会调用此方法。
     * params.startPosition从第几行还是加载，params.loadSize加载多少行
     */
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Student>) {
        callback.onResult(fetchItems(params.startPosition, params.loadSize))
        Log.e("测试", "params.startPosition${params.startPosition}---params.loadSize${params.loadSize}")
    }

    /**
     * 加载初始化数据，可以这么来理解，加载的是第一页的数据。形象的说，当我们第一次打开页面，需要回调此方法来获取数据。
     */
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Student>) {
        callback.onResult(fetchItems(0, 20), 0, 2000)
    }

    private fun fetchItems(startPosition: Int, pageSize: Int): MutableList<Student> {
        val list = mutableListOf<Student>()
        for (i in startPosition until startPosition + pageSize) {
            val student = Student(i.toString(), "测试$i", "")
            list.add(student)
        }
        return list
    }
}