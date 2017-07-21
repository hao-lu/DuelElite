package com.example.haolu.duelmaster

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.text.DecimalFormat

class LifePointCalculator : BaseObservable() {

    data class Log(var log: MutableList<LifePointCalculator.LogItem>) : Parcelable {
        fun add(l: LifePointCalculator.LogItem) {
            log.add(l)
        }

        companion object {
            @JvmField val CREATOR: Parcelable.Creator<Log> = object : Parcelable.Creator<Log> {
                override fun createFromParcel(source: Parcel): Log = Log(source)
                override fun newArray(size: Int): Array<Log?> = arrayOfNulls(size)
            }
        }

        constructor(source: Parcel) : this(
        ArrayList<LifePointCalculator.LogItem>().apply { source.readList(this, LifePointCalculator.LogItem::class.java.classLoader) }
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeList(log)
        }
    }

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

    @get:Bindable
    var playerOneLP: Double = 8000.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerOneLP)
        }

    @get:Bindable
    var playerTwoLP: Double = 8000.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerTwoLP)
        }

    @get:Bindable
    var cumulatedLP: Double = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.cumulatedLP)
        }

    @get:Bindable
//    var log: MutableList<LogItem> = mutableListOf()
    var log: Log = Log(mutableListOf())
    set(value) {
        field = value
        notifyPropertyChanged(BR.log)
    }

    val LP_FORMAT: DecimalFormat = DecimalFormat("0000")

    fun updateLP(operation: Boolean, isPlayerOne: Boolean) {
        if (isPlayerOne) {
            if (operation) playerOneLP += cumulatedLP else playerOneLP -= cumulatedLP
        } else {
            if (operation) playerTwoLP += cumulatedLP else playerTwoLP -= cumulatedLP
        }

        log(operation, isPlayerOne)
        // reset after updating life points
        cumulatedLP = 0.0
    }

    fun updateCumulatedLP(lp: Double) {
        cumulatedLP += lp
    }

    fun resetCumulatedLP() {
        cumulatedLP = 0.0
    }

    fun reset() {
        playerOneLP = 8000.0
        playerTwoLP = 8000.0
        cumulatedLP = 0.0

    }

    private fun log(operation: Boolean, isPlayerOne: Boolean) {
        var op = ""
        var player = ""
        if (operation) op = "GAINED" else op = "LOST"
        if (isPlayerOne) player = "PLAYER ONE" else player = "PLAYER TWO"
        log.add(LifePointCalculator.LogItem("$player $op ${LP_FORMAT.format(cumulatedLP)}"))
    }

    fun printLog() {
        for (l in log.log) {
            println(l)
        }
    }
}