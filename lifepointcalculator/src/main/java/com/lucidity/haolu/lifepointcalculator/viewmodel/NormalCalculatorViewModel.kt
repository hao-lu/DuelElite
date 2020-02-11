package com.lucidity.haolu.lifepointcalculator.viewmodel

import com.lucidity.haolu.lifepointcalculator.model.NormalLifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.Player

class NormalCalculatorViewModel : CalculatorViewModel() {

    private val calculator = NormalLifePointCalculator()

    override fun onNumberClicked(num: String) {
        calculator.appendDigit(num)
        _actionLp.value = calculator.getActionLp()
    }

    override fun onAddClicked(player: Player) {
        setPreviousLps(player)
        calculator.addLp(player)
        updateLpView(player)
    }

    override fun onSubtractClicked(player: Player) {
        setPreviousLps(player)
        calculator.subtractLp(player)
        updateLpView(player)
    }

    override fun onClearClicked() {
        calculator.clearActionLp()
        _actionLp.value = calculator.getActionLp()
    }

    private fun updateLpView(player: Player) {
        if (player == Player.ONE) _playerOneLp.value = calculator.playerOneLp
        else _playerTwoLp.value = calculator.playerTwoLp
        _actionLp.value = calculator.getActionLp()
    }

    private fun setPreviousLps(player: Player) {
        _previousPlayerLp = if (player == Player.ONE) calculator.playerOneLp
        else calculator.playerTwoLp
    }
}