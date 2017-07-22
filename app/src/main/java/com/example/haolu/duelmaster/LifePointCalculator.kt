package com.example.haolu.duelmaster

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
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
        source.createTypedArrayList(LogItem.CREATOR)
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeTypedList(log)
        }
    }

    data class LogItem(val player: String, val operation: String, val turnLP: Int, val totalLP: Int) : Parcelable {

        companion object {
            @JvmField val CREATOR: Parcelable.Creator<LogItem> = object : Parcelable.Creator<LogItem> {
                override fun createFromParcel(source: Parcel): LogItem = LogItem(source)
                override fun newArray(size: Int): Array<LogItem?> = arrayOfNulls(size)
            }
        }

        constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readInt()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(player)
            dest.writeString(operation)
            dest.writeInt(turnLP)
            dest.writeInt(totalLP)
        }

    }

    @get:Bindable
    var playerOneLP: Int = 8000
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerOneLP)
        }

    @get:Bindable
    var playerTwoLP: Int = 8000
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerTwoLP)
        }

    @get:Bindable
    var cumulatedLP: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.cumulatedLP)
        }

    @get:Bindable
    var log: Log = Log(mutableListOf())
        set(value) {
            field = value
            notifyPropertyChanged(BR.log)
        }

    val LP_FORMAT: DecimalFormat = DecimalFormat("0")
    var halve: Boolean = false

    fun updateLP(operation: Boolean, isPlayerOne: Boolean) {
        if (!halve) {
            if (isPlayerOne) {
                if (operation) playerOneLP += cumulatedLP else playerOneLP -= cumulatedLP
            } else {
                if (operation) playerTwoLP += cumulatedLP else playerTwoLP -= cumulatedLP
            }
        }
         else {
            if (isPlayerOne) {
                playerOneLP /= 2
                cumulatedLP = playerOneLP
            }
            else {
                playerTwoLP /= 2
                cumulatedLP = playerTwoLP
            }
        }

        log(operation, isPlayerOne)
        // reset after updating life points
        halve = false
        cumulatedLP = 0
    }

    fun updateCumulatedLP(lp: Int) {
        cumulatedLP += lp
    }

    fun resetCumulatedLP() {
        cumulatedLP = 0
    }

    fun reset() {
        playerOneLP = 8000
        playerTwoLP = 8000
        cumulatedLP = 0
        log.log.clear()
    }

    private fun log(operation: Boolean, isPlayerOne: Boolean) {
        var op = ""
        var player = ""
        var lp = 0
        if (operation) op = "GAINED" else op = "LOST"
        if (isPlayerOne) {
            player = "PLAYER ONE"
            lp = playerOneLP
        }
        else {
            player = "PLAYER TWO"
            lp = playerTwoLP
        }
        log.add(LifePointCalculator.LogItem(player, op, cumulatedLP, lp))
    }

    fun printLog() {
        for (l in log.log) {
            println(l)
        }
    }
}