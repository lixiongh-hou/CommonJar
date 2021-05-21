package com.common.jar.play_music.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @author ���ۺ�
 *
 * @features �����б�
 */
class MusicList(
    val id: String?,
    /**��������*/
    val name: String?,
    /**
     * ���˺����������
     */
    val filterName: String?,
    /**���ֵ�ַ*/
    val path: String?,
    /**����*/
    val singer: String?,
    /**��������*/
    val duration: Int,
    /**�ļ���С*/
    val size: Long,
    /**ר��id*/
    val albumId: String?,
    /**
     * ר��
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
