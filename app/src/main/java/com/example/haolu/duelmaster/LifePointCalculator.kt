package com.example.haolu.duelmaster

import android.databinding.BaseObservable
import android.databinding.Bindable
import java.text.DecimalFormat

class LifePointCalculator : BaseObservable() {

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
    var log: MutableList<LogItem> = mutableListOf()
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
        log.add(LogItem("$player $op ${LP_FORMAT.format(cumulatedLP)}"))
    }

    fun printLog() {
        for (l in log) {
            println(l)
        }
    }
}