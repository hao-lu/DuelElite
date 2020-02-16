package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem
import com.lucidity.haolu.lifepointcalculator.model.Player

class CalculatorViewModel : ViewModel() {

    private val calculator = LifePointCalculator()
    private val log: MutableList<LifePointLogItem> = mutableListOf()

    val timer = PausableCountDownTimer()
    private val _playerOneLp: MutableLiveData<Triple<Int, Int, Int>> = MutableLiveData()
    val playerOneLp: LiveData<Triple<Int, Int, Int>> = _playerOneLp
    private val _playerTwoLp: MutableLiveData<Triple<Int, Int, Int>> = MutableLiveData()
    val playerTwoLp: LiveData<Triple<Int, Int, Int>> = _playerTwoLp
    private val _actionLp: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val actionLp: LiveData<Pair<Int, Int>> = _actionLp
    private val _halve: MutableLiveData<Int> = MutableLiveData()
    val halve = _halve

    fun onNumberClicked(num: String) {
        _halve.value = R.string.empty
        when (CalculatorInput.ACCUMULATED) {
            CalculatorInput.NORMAL -> {
                val appendNum = appendNum(_actionLp.value?.second, num)
                if (isLessThanMaxLifePointOrNull(appendNum)) {
                    _actionLp.value = Pair(appendNum, appendNum)
                }
            }
            CalculatorInput.ACCUMULATED -> {
                val sumNum = sumNum(_actionLp.value?.second, num)
                if (isLessThanMaxLifePointOrNull(sumNum)) {
                    val previousLp =
                        if (_actionLp.value?.second == null) 0 else _actionLp.value?.second!!
                    _actionLp.value = Pair(previousLp, sumNum)
                }
            }
        }
    }

    private fun appendNum(lp: Int?, num: String): Int {
        return if (lp != null) "$lp$num".toInt() else num.toInt()
    }

    private fun sumNum(lp: Int?, num: String): Int {
        return if (lp != null) lp + num.toInt() else num.toInt()
    }

    private fun isLessThanMaxLifePointOrNull(lp: Int?) =
        (lp == null || lp < LifePointCalculator.MAX_LIFE_POINT)

    fun onAddClicked(player: Player) {
        _actionLp.value?.let { lp ->
            val previousLp = calculator.getPlayerLifePoint(player)
            calculator.addLp(player, lp.second)
            val currLp = calculator.getPlayerLifePoint(player)
            updateLpView(player, previousLp, currLp, R.drawable.ic_arrow_drop_up)
        }
    }

    fun onSubtractClicked(player: Player) {
        _actionLp.value?.let { lp ->
            val previousLp = calculator.getPlayerLifePoint(player)
            if (isHalveOrNull(_halve.value)) {
                calculator.subtractLp(player, previousLp / 2)
            } else {
                calculator.subtractLp(player, lp.second)
            }
            val currLp = calculator.getPlayerLifePoint(player)
            updateLpView(player, previousLp, currLp, R.drawable.ic_arrow_drop_down)
            logLp(player.name, previousLp - currLp, currLp, timer.formattedRemainingTime)
        }
    }

    private fun isHalveOrNull(resourceId: Int?) =
        resourceId == R.string.halve || resourceId == null

    fun onClearClicked() {
        _actionLp.value =
            Pair(LifePointCalculator.MIN_LIFE_POINT, LifePointCalculator.MIN_LIFE_POINT)
    }

    fun reset() {
        calculator.reset()
        timer.cancel()
    }

    fun onTimerClicked() {
        if (timer.isRunning) timer.pause() else timer.start()
    }

    fun onHalveClicked() {
        onClearClicked()
        _halve.value = R.string.halve
    }

    private fun updateLpView(player: Player, previousLp: Int, currLp: Int, drawableId: Int) {
        if (player == Player.ONE) _playerOneLp.value = Triple(previousLp, currLp, drawableId)
        else _playerTwoLp.value = Triple(previousLp, currLp, drawableId)
        val previousActionLp = if (_actionLp.value?.second == null) 0 else _actionLp.value?.second!!
        _actionLp.value = Pair(previousActionLp, 0)
    }

    private fun logLp(player: String, actionLp: Int, totalLp: Int, time: String) {
        log.add(LifePointLogItem(player, actionLp, totalLp, time))
    }

    enum class CalculatorInput {
        NORMAL,
        ACCUMULATED
    }

}