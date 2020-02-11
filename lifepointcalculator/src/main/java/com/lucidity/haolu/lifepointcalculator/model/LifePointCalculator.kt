package com.lucidity.haolu.lifepointcalculator.model

abstract class LifePointCalculator {

    companion object {
        const val START_LP = 8000
        const val MIN_LIFE_POINT = 0
        const val MAX_LIFE_POINT = 999999
    }

    protected var _playerOneLp = START_LP
    val playerOneLp
        get() = _playerOneLp
    protected var _playerTwoLp = START_LP
    val playerTwoLp
        get() = _playerTwoLp

    fun addLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> _playerOneLp = add(playerOneLp, turnLp)
            Player.TWO -> _playerTwoLp = add(playerTwoLp, turnLp)
        }
    }

    private fun add(currLp: Int, turnLp: Int) =
            if (currLp + turnLp > MAX_LIFE_POINT) MAX_LIFE_POINT else currLp + turnLp

    fun subtractLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> _playerOneLp = subtract(playerOneLp, turnLp)
            Player.TWO -> _playerTwoLp = subtract(playerTwoLp, turnLp)
        }
    }

    private fun subtract(currLp: Int, turnLp: Int) =
            if (currLp - turnLp < MIN_LIFE_POINT) MIN_LIFE_POINT else currLp - turnLp

    open fun reset() {
        _playerOneLp = START_LP
        _playerTwoLp = START_LP
    }
}

enum class Player {
    ONE,
    TWO
}
