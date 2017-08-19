package com.lucidity.haolu.duelking

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import java.text.DecimalFormat

class LifePointCalculator : BaseObservable() {

    // List of mLog items
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

    // Log item, which contains player, operation, the cumulatedLp for that turn, and totalLP
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
    var playerOneLp: Int = 8000
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerOneLp)
        }

    @get:Bindable
    var playerTwoLp: Int = 8000
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerTwoLp)
        }

    @get:Bindable
    var cumulatedLp: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.cumulatedLp)
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
                if (operation) playerOneLp += cumulatedLp else playerOneLp -= cumulatedLp
            } else {
                if (operation) playerTwoLp += cumulatedLp else playerTwoLp -= cumulatedLp
            }
        }
         else {
            if (isPlayerOne) {
                playerOneLp /= 2
                cumulatedLp = playerOneLp
            }
            else {
                playerTwoLp /= 2
                cumulatedLp = playerTwoLp
            }
        }

//        if (playerOneLp < 0) playerOneLp = 0
//        if (playerTwoLp < 0) playerTwoLp = 0

        log(operation, isPlayerOne)
        // reset after updating life points
        halve = false
        cumulatedLp = 0
    }

    fun updateCumulatedLP(lp: Int) {
        cumulatedLp += lp
    }

    fun resetCumulatedLP() {
        cumulatedLp = 0
    }

    fun reset() {
        playerOneLp = 8000
        playerTwoLp = 8000
        cumulatedLp = 0
        log.log.clear()
    }

    private fun log(operation: Boolean, isPlayerOne: Boolean) {
        var op = ""
        var player = ""
        var lp = 0
        if (operation) op = "+" else op = "-"
        if (isPlayerOne) {
            player = "Player 1"
            lp = playerOneLp
        }
        else {
            player = "Player 2"
            lp = playerTwoLp
        }
        if (cumulatedLp != 0)
            log.add(LifePointCalculator.LogItem(player, op, cumulatedLp, lp))
    }

    fun printLog() {
        for (l in log.log) {
            println(l)
        }
    }
}