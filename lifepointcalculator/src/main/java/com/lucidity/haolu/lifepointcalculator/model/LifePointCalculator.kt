package com.lucidity.haolu.lifepointcalculator.model

open class LifePointCalculator {

    companion object {
        private const val MIN_LIFE_POINT = 0
        private const val MAX_LIFE_POINT = 999999
    }

    private var startLp = 8000
    private var playerOneLp = startLp
    private var playerTwoLp = startLp
    private var cumulatedTurnLp = 0

    fun addLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp = add(playerOneLp, cumulatedTurnLp)
            Player.TWO -> playerTwoLp = add(playerTwoLp, cumulatedTurnLp)
        }
    }

    fun addLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> playerOneLp = add(playerOneLp, turnLp)
            Player.TWO -> playerTwoLp = add(playerTwoLp, turnLp)
        }
    }

    private fun add(currLp: Int, turnLp: Int) =
            if (currLp + turnLp > MAX_LIFE_POINT) MAX_LIFE_POINT else currLp + turnLp

    fun subtractLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp = subtract(playerOneLp, cumulatedTurnLp)
            Player.TWO -> playerTwoLp = subtract(playerTwoLp, cumulatedTurnLp)
        }
    }

    fun subtractLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> playerOneLp = subtract(playerOneLp, turnLp)
            Player.TWO -> playerTwoLp = subtract(playerTwoLp, turnLp)
        }
    }

    private fun subtract(currLp: Int, turnLp: Int) =
            if (currLp - turnLp < MIN_LIFE_POINT) MIN_LIFE_POINT else currLp - turnLp

    fun halveLp(player: Player) {
        when (player) {
            Player.ONE -> playerOneLp /= 2
            Player.TWO -> playerTwoLp /= 2
        }
    }

    fun addCumulatedTurnLp(turnLp: Int) {
        cumulatedTurnLp += turnLp
    }

    fun getPlayerOneLp(): Int {
        return playerOneLp
    }

    fun getPlayerTwo(): Int {
        return playerTwoLp
    }

    fun getCumulatedTurnLp(): Int {
        return cumulatedTurnLp
    }

    open fun reset() {
        playerOneLp = startLp
        playerTwoLp = startLp
        cumulatedTurnLp = 0
    }
}

enum class Player {
    ONE,
    TWO
}
