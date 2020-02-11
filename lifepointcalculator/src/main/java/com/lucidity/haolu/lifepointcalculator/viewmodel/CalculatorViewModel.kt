package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.model.Player

abstract class CalculatorViewModel : ViewModel() {

    private val DUEL_TIME = 2400000L
    private val INTERVAL_TIME = 1000L
    val timer = PausableCountDownTimer(DUEL_TIME, INTERVAL_TIME)

    protected val _playerOneLp: MutableLiveData<Int> = MutableLiveData()
    val playerOneLp: LiveData<Int> = _playerOneLp
    protected val _playerTwoLp: MutableLiveData<Int> = MutableLiveData()
    val playerTwoLp: LiveData<Int> = _playerTwoLp
    protected val _actionLp: MutableLiveData<Int> = MutableLiveData()
    val actionLp: LiveData<Int> = _actionLp
    protected var _previousPlayerLp: Int = 0
    val previousPlayerLp
        get() = _previousPlayerLp
    protected var _previousActionLp: Int = 0
    val previousActionLp
        get() = _previousActionLp

    abstract fun onNumberClicked(num: String)
    abstract fun onAddClicked(player: Player)
    abstract fun onSubtractClicked(player: Player)
    abstract fun onClearClicked()

    open fun reset() {
        timer.cancel()
    }

    fun onTimerClicked() {
        if (timer.isRunning) timer.pause() else timer.start()
    }
}