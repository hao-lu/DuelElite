package com.lucidity.haolu.lifepointcalculator.model
 
class LifePointCalculator {

    companion object {
        const val START_LP = 8000
        const val MIN_LIFE_POINT = 0
        const val MAX_LIFE_POINT = 999999
    }

    private var playerOneLp = START_LP
    private var playerTwoLp = START_LP

    fun addLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> playerOneLp = add(playerOneLp, turnLp)
            Player.TWO -> playerTwoLp = add(playerTwoLp, turnLp)
        }
    }

    private fun add(currLp: Int, turnLp: Int) =
            if (currLp + turnLp > MAX_LIFE_POINT) MAX_LIFE_POINT else currLp + turnLp

    fun subtractLp(player: Player, turnLp: Int) {
        when (player) {
            Player.ONE -> playerOneLp = subtract(playerOneLp, turnLp)
            Player.TWO -> playerTwoLp = subtract(playerTwoLp, turnLp)
        }
    }

    private fun subtract(currLp: Int, turnLp: Int) =
            if (currLp - turnLp < MIN_LIFE_POINT) MIN_LIFE_POINT else currLp - turnLp

    fun reset() {
        playerOneLp = START_LP
        playerTwoLp = START_LP
    }

    fun getPlayerLifePoint(player: Player): Int {
        return if (player == Player.ONE) playerOneLp else playerTwoLp
    }
}

enum class Player(val tag: String) {
    ONE("Player 1"),
    TWO("Player 2")
}
