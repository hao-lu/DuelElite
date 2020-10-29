package com.lucidity.haolu.lifepointcalculator.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LifePointLog(private val log: MutableList<LifePointLogItem>) : Parcelable {

    fun add(l: LifePointLogItem) = log.add(l)

    fun getList() = log.toList()

    fun getReverseList() = log.reversed()

    fun getLatestEntry(): LifePointLogItem? {
        return if (log.isEmpty()) null else log.last()
    }

    fun reset() = log.clear()

    override fun describeContents() = 0
}

