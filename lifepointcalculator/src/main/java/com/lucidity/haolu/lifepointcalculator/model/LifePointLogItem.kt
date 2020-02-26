package com.lucidity.haolu.lifepointcalculator.model

import android.os.Parcel
import android.os.Parcelable

data class LifePointLogItem(
    val player: String,
    val actionLp: Int,
    val totalLp: Int,
    val time: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt(),
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(player)
        writeInt(actionLp)
        writeInt(totalLp)
        writeString(time)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LifePointLogItem> =
            object : Parcelable.Creator<LifePointLogItem> {
                override fun createFromParcel(source: Parcel): LifePointLogItem =
                    LifePointLogItem(source)

                override fun newArray(size: Int): Array<LifePointLogItem?> = arrayOfNulls(size)
            }
    }
}
