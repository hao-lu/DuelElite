package com.lucidity.haolu.lifepointcalculator.viewmodel

import com.lucidity.haolu.lifepointcalculator.SingleLiveEvent
import com.lucidity.haolu.lifepointcalculator.model.CumulatedLifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.Player

class CumulatedCalculatorViewModel : CalculatorViewModel() {

    private val calculator = CumulatedLifePointCalculator()
    val isHalve: SingleLiveEvent<Void> = SingleLiveEvent()

    override fun reset() {
        calculator.reset()
        _previousActionLp = calculator.actionLp
        _previousPlayerLp = calculator.playerOneLp
    }

    override fun onNumberClicked(num: String) {
        _previousActionLp = calculator.actionLp
        calculator.addCumulatedTurnLp(num.toInt())
        _actionLp.value = calculator.actionLp
    }

    override fun onAddClicked(player: Player) {
        setPreviousLps(player)
        calculator.addLp(player)
        updateLpView(player)
    }

    override fun onSubtractClicked(player: Player) {
        setPreviousLps(player)
        if (calculator.isHalve) calculator.halveLp(player) else calculator.subtractLp(player)
        updateLpView(player)
    }

    override fun onClearClicked() {
        calculator.clearCumulatedTurnLp()
        _previousActionLp = calculator.actionLp
        _actionLp.value = calculator.actionLp
    }

    private fun updateLpView(player: Player) {
        if (player == Player.ONE) _playerOneLp.value = calculator.playerOneLp
        else _playerTwoLp.value = calculator.playerTwoLp
        _actionLp.value = calculator.actionLp
    }

    fun onHalveClicked() {
        calculator.setHalve()
        isHalve.call()
    }

    // Used to decrement animation of life points
    private fun setPreviousLps(player: Player) {
        _previousPlayerLp = if (player == Player.ONE) calculator.playerOneLp
        else calculator.playerTwoLp
        _previousActionLp = if (calculator.isHalve) 0 else calculator.actionLp
    }
}