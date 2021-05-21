package com.common.jar.play_music.config

import android.content.Context
import android.provider.MediaStore
import com.common.jar.play_music.data.MusicList
import com.common.jar.play_music.filterName
import com.common.jar.play_music.getAlbumArt
import com.common.tool.event_live_bridge.EventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author 李雄厚
 *
 * @features 只需要本地访问在这执行
 */
object LocalConfig {

    val notifyUpdateUi = EventLiveData<MusicList?>()

    private suspend fun runInDispatcherIO(block: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            block.invoke()
        }
    }

    suspend fun queryMusicList(context: Context, success: (MutableList<MusicList>) -> Unit){
        runInDispatcherIO {
            try {
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                )?.run {
                    val list = mutableListOf<MusicList>()
                    while (moveToNext()) {
                        val name = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                        val id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        val singer = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                        val path = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        val duration = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        val size = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                        val albumId = getLong(getColumnIndex(MediaStore.Audio.Albums._ID));
//                        val albumId = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                        val album = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                        //过滤下歌曲名称
                        name.replace(" ", "")
                        val filterName = name.filterName()
                        list.add(
                            MusicList(
                                id.toString(), name, filterName, path, singer, duration, size,
                                albumId.toString(),album
                            )
                        )
                    }
                    success.invoke(list)
                    close()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}