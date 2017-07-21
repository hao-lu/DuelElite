package com.example.haolu.duelmaster

import android.os.Parcel
import android.os.Parcelable

data class LogItem(val logItem: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<LogItem> = object : Parcelable.Creator<LogItem> {
            override fun createFromParcel(source: Parcel): LogItem = LogItem(source)
            override fun newArray(size: Int): Array<LogItem?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(logItem)
    }
}