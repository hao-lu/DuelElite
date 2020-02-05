package com.lucidity.haolu.lifepointcalculator.model

open class LifePointCalculator {

    companion object {
        const val START_LP = 8000
        const val MIN_LIFE_POINT = 0
        const val MAX_LIFE_POINT = 999999
        const val HALVE_LIFE_POINT = -1
    }

    private var playerOneLp = START_LP
    private var playerTwoLp = START_LP
    private var cumulatedTurnLp = MIN_LIFE_POINT

    fun addLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp = add(playerOneLp, cumulatedTurnLp)
            Player.TWO -> playerTwoLp = add(playerTwoLp, cumulatedTurnLp)
        }
        cumulatedTurnLp = MIN_LIFE_POINT
    }

    private fun add(currLp: Int, turnLp: Int) =
            if (currLp + turnLp > MAX_LIFE_POINT) MAX_LIFE_POINT else currLp + turnLp

    fun subtractLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp = subtract(playerOneLp, cumulatedTurnLp)
            Player.TWO -> playerTwoLp = subtract(playerTwoLp, cumulatedTurnLp)
        }
        cumulatedTurnLp = MIN_LIFE_POINT
    }

    private fun subtract(currLp: Int, turnLp: Int) =
            if (currLp - turnLp < MIN_LIFE_POINT) MIN_LIFE_POINT else currLp - turnLp

    fun halveLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp /= 2
            Player.TWO -> playerTwoLp /= 2
        }
        cumulatedTurnLp = MIN_LIFE_POINT
    }

    fun addCumulatedTurnLp(turnLp: Int) {
        if (isCumulatedHalve()) cumulatedTurnLp = MIN_LIFE_POINT // Reset -1+2000 = 1999
        if (cumulatedTurnLp + turnLp < MAX_LIFE_POINT) cumulatedTurnLp += turnLp
    }

    fun getPlayerOneLp(): Int {
        return playerOneLp
    }

    fun getPlayerTwoLp(): Int {
        return playerTwoLp
    }

    fun getCumulatedTurnLp(): Int {
        return cumulatedTurnLp
    }

    fun clearCumulatedTurnLp() {
        cumulatedTurnLp = MIN_LIFE_POINT
    }

    fun setCumulatedToHalve() {
        cumulatedTurnLp = HALVE_LIFE_POINT
    }

    fun isCumulatedHalve(): Boolean {
        return cumulatedTurnLp == HALVE_LIFE_POINT
    }

    open fun reset() {
        playerOneLp = START_LP
        playerTwoLp = START_LP
        cumulatedTurnLp = MIN_LIFE_POINT
    }
}

enum class Player {
    ONE,
    TWO
}
