package com.common.tool.notify

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


/**
 * @author 李雄厚
 *
 * @features 提醒实体类
 */
data class Reminder(
    val reminderId: String,
    var time: String,
    /**
     * 提规则
     */
    val rule: String = "只响一次",
    /**
     * 提示
     */
    val remarks: String = "",

    var turnNn: Boolean = false

) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readBoolean()
    )

    var timeLong = -1L

    var selectedDel = false

    var selected = false



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reminderId)
        parcel.writeString(time)
        parcel.writeString(rule)
        parcel.writeString(remarks)
        parcel.writeBoolean(turnNn)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Reminder(reminderId='$reminderId', time='$time', rule='$rule', remarks='$remarks', turnNn='$turnNn')"
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}
