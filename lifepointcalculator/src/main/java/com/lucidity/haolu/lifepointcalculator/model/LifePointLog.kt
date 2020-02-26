package com.lucidity.haolu.lifepointcalculator.model

import android.os.Parcel
import android.os.Parcelable

data class LifePointLog(private val log: MutableList<LifePointLogItem>) : Parcelable {

    fun add(l: LifePointLogItem) = log.add(l)

    fun getList() = log.toList()

    fun getReverseList() = log.reversed()

    fun getLatestEntry() = log.last()

    constructor(source: Parcel) : this(
        source.createTypedArrayList(LifePointLogItem.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(log)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LifePointLog> = object : Parcelable.Creator<LifePointLog> {
            override fun createFromParcel(source: Parcel): LifePointLog = LifePointLog(source)
            override fun newArray(size: Int): Array<LifePointLog?> = arrayOfNulls(size)
        }
    }
}

