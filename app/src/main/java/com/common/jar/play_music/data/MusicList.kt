package com.common.jar.play_music.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @author 李雄厚
 *
 * @features 音乐列表
 */
class MusicList(
    val id: String?,
    /**音乐名称*/
    val name: String?,
    /**
     * 过滤后的音乐名称
     */
    val filterName: String?,
    /**音乐地址*/
    val path: String?,
    /**歌手*/
    val singer: String?,
    /**歌曲长度*/
    val duration: Int,
    /**文件大小*/
    val size: Long,
    /**专辑id*/
    val albumId: String?,
    /**
     * 专辑
     */
    var album: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(filterName)
        parcel.writeString(path)
        parcel.writeString(singer)
        parcel.writeInt(duration)
        parcel.writeLong(size)
        parcel.writeString(albumId)
        parcel.writeString(album)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicList> {
        override fun createFromParcel(parcel: Parcel): MusicList {
            return MusicList(parcel)
        }

        override fun newArray(size: Int): Array<MusicList?> {
            return arrayOfNulls(size)
        }
    }

}
