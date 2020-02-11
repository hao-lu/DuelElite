package com.lucidity.haolu.lifepointcalculator.model

class CumulatedLifePointCalculator : LifePointCalculator() {

    var isHalve: Boolean = false
        private set

    var actionLp = MIN_LIFE_POINT
        private set

    fun halveLp(player: Player) {
        when (player) {
            Player.ONE -> _playerOneLp /= 2
            Player.TWO -> _playerTwoLp /= 2
        }
        actionLp = MIN_LIFE_POINT
        isHalve = false
    }

    fun addLp(player: Player) {
        addLp(player, actionLp)
        actionLp = MIN_LIFE_POINT
    }

    fun subtractLp(player: Player) {
        subtractLp(player, actionLp)
        actionLp = MIN_LIFE_POINT
    }

    fun addCumulatedTurnLp(turnLp: Int) {
        if (actionLp + turnLp < MAX_LIFE_POINT) actionLp += turnLp
        isHalve = false
    }

    fun setHalve() {
        actionLp = 0
        isHalve = true
    }

    fun clearCumulatedTurnLp() {
        actionLp = MIN_LIFE_POINT
    }

    override fun reset() {
        super.reset()
        actionLp = MIN_LIFE_POINT
    }

}