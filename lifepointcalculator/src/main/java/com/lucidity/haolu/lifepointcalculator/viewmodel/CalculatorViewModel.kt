package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.Player

class CalculatorViewModel : ViewModel() {

    private val calculator = LifePointCalculator()

    private val _cumulatedLp: MutableLiveData<Int> = MutableLiveData()
    val cumulatedLp: LiveData<Int> = _cumulatedLp

    private val _playerOneLp: MutableLiveData<Int> = MutableLiveData()
    val playerOneLp: LiveData<Int> = _playerOneLp

    private val _playerTwoLp: MutableLiveData<Int> = MutableLiveData()
    val playerTwoLp: LiveData<Int> = _playerTwoLp

    var previousCumulatedLp: Int = 0
        private set

    var previousPlayerLp: Int = 0
        private set

    fun reset() {
        calculator.reset()
        previousCumulatedLp = calculator.getCumulatedTurnLp()
        previousPlayerLp = calculator.getPlayerOneLp()
    }

    fun onPresetNumberClicked(lp: String) {
        previousCumulatedLp = calculator.getCumulatedTurnLp()
        calculator.addCumulatedTurnLp(lp.toInt())
        _cumulatedLp.value = calculator.getCumulatedTurnLp()
    }

    fun onAddClicked(player: Player) {
        setPreviousLps(player)
        calculator.addLp(player)
        updateLpView(player)
    }

    fun onSubtractClicked(player: Player) {
        setPreviousLps(player)
        if (calculator.isCumulatedHalve()) calculator.halveLp(player) else calculator.subtractLp(player)
        updateLpView(player)
    }

    private fun updateLpView(player: Player) {
        if (player == Player.ONE) _playerOneLp.value = calculator.getPlayerOneLp()
        else _playerTwoLp.value = calculator.getPlayerTwoLp()
        _cumulatedLp.value = calculator.getCumulatedTurnLp()
    }

    fun onHalveClicked() {
        calculator.setCumulatedToHalve()
        _cumulatedLp.value = calculator.getCumulatedTurnLp()
    }

    fun onClearClicked() {
        calculator.clearCumulatedTurnLp()
        previousCumulatedLp = calculator.getCumulatedTurnLp()
        _cumulatedLp.value = calculator.getCumulatedTurnLp()
    }

    // Used to decrement animation of life points
    private fun setPreviousLps(player: Player) {
        previousPlayerLp = if (player == Player.ONE) calculator.getPlayerOneLp()
        else calculator.getPlayerTwoLp()
        previousCumulatedLp = if (calculator.isCumulatedHalve()) 0 else calculator.getCumulatedTurnLp()
    }
}