package com.lucidity.haolu.lifepointcalculator.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LifePointLogItem(
    val player: String,
    val actionLp: Int,
    val totalLp: Int,
    val time: String
) : Parcelable
