package com.lucidity.haolu.lifepointcalculator.model

import java.lang.StringBuilder

class NormalLifePointCalculator : LifePointCalculator() {

    private val numStrBuilder: StringBuilder = StringBuilder()

    fun appendDigit(digit: String) {
        digit.toIntOrNull()?.let {
            numStrBuilder.append(digit)
        }
    }

//    fun getActionLp(): String = numStrBuilder.toString()

    fun clearActionLp() = numStrBuilder.clear()

    fun getActionLp(): Int = if (numStrBuilder.isEmpty()) 0 else numStrBuilder.toString().toInt()

    fun addLp(player: Player) {
        addLp(player, getActionLp())
        clearActionLp()
    }

    fun subtractLp(player: Player) {
        subtractLp(player,getActionLp())
        clearActionLp()
    }

    override fun reset() {
        super.reset()
        clearActionLp()
    }
}