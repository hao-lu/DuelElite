package com.lucidity.haolu.duelking

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import java.text.DecimalFormat


/**
 * Model class for a life point calculator for a duel
 */

class LifePointCalculator : BaseObservable() {

    private val START_LP = 8000

    /**
     * List of mLog items
     * Parcelable to pass to LogActivity
     *
     */
    data class Log(var mLog: MutableList<LifePointCalculator.LogItem>) : Parcelable {

        fun add(l: LifePointCalculator.LogItem) {
            mLog.add(l)
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
            dest.writeTypedList(mLog)
        }
    }

    /**
     * Log item containing: mPlayer, mOperation, cumulatedLP, totalLp
     */

    data class LogItem(val mPlayer: String, val mOperation: String, val mTurnLp: Int, val mTotalLp: Int) : Parcelable {

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
            dest.writeString(mPlayer)
            dest.writeString(mOperation)
            dest.writeInt(mTurnLp)
            dest.writeInt(mTotalLp)
        }

    }

    @get:Bindable
    var mPlayerOneLp: Int = START_LP
        set(value) {
            field = value
            notifyPropertyChanged(BR.mPlayerOneLp)
        }

    @get:Bindable
    var mPlayerTwoLp: Int = START_LP
        set(value) {
            field = value
            notifyPropertyChanged(BR.mPlayerTwoLp)
        }

    @get:Bindable
    var mCumulatedLp: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mCumulatedLp)
        }

    @get:Bindable
    var mLog: Log = Log(mutableListOf())
        set(value) {
            field = value
            notifyPropertyChanged(BR.mLog)
        }

    val LP_FORMAT: DecimalFormat = DecimalFormat("0")

    fun updateLP(operation: Boolean, isPlayerOne: Boolean, halve: Boolean) {
        if (!halve) {
            if (isPlayerOne) {
                if (operation) mPlayerOneLp += mCumulatedLp else mPlayerOneLp -= mCumulatedLp
            } else {
                if (operation) mPlayerTwoLp += mCumulatedLp else mPlayerTwoLp -= mCumulatedLp
            }
        }
         else {
            if (isPlayerOne) {
                val halfLp = mPlayerOneLp / 2
                if (operation) mPlayerOneLp += halfLp else mPlayerOneLp -= halfLp
                // For logging
                mCumulatedLp = halfLp
            }
            else {
                val halfLp = mPlayerTwoLp / 2
                if (operation) mPlayerTwoLp += halfLp else mPlayerTwoLp -= halfLp
                mCumulatedLp = halfLp
            }
        }

        // Check for negative lp
        if (mPlayerOneLp < 0) mPlayerOneLp = 0
        if (mPlayerTwoLp < 0) mPlayerTwoLp = 0

        log(operation, isPlayerOne)
        // reset after updating life points
        mCumulatedLp = 0
    }

    fun updateCumulatedLP(lp: Int) {
        mCumulatedLp += lp
    }

    fun resetCumulatedLP() {
        mCumulatedLp = 0
    }

    fun reset() {
        mPlayerOneLp = START_LP
        mPlayerTwoLp = START_LP
        mCumulatedLp = 0
        mLog.mLog.clear()
    }

    private fun log(operation: Boolean, isPlayerOne: Boolean) {
        val op: String = if (operation) "+" else "-"
        val player: String = if (isPlayerOne) "Player 1" else "Player 2"
        val lp: Int = if (isPlayerOne) mPlayerOneLp else mPlayerTwoLp
        if (mCumulatedLp != 0)
            mLog.add(LifePointCalculator.LogItem(player, op, mCumulatedLp, lp))
    }

}