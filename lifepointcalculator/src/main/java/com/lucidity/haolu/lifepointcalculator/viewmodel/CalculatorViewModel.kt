package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem
import com.lucidity.haolu.lifepointcalculator.model.LifePointLog
import com.lucidity.haolu.lifepointcalculator.model.Player

class CalculatorViewModel : ViewModel() {

    val calculator = LifePointCalculator()
//    val log: MutableList<LifePointLogItem> = mutableListOf()
    val log: LifePointLog = LifePointLog(mutableListOf())
    val timer = PausableCountDownTimer()

    private val _playerOneLp: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val playerOneLp: LiveData<Pair<Int, Int>> = _playerOneLp
    private val _playerTwoLp: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val playerTwoLp: LiveData<Pair<Int, Int>> = _playerTwoLp
    private val _actionLp: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val actionLp: LiveData<Pair<Int, Int>> = _actionLp
    private val _actionLpHint: MutableLiveData<LifePointLogItem> = MutableLiveData()
    val actionLpHint: LiveData<LifePointLogItem> = _actionLpHint

    var animate: Boolean = false
        private set
    var halve = false
        private set

    fun onNumberClicked(num: String) {
        halve = false
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
            updatePlayerLpView(player, previousLp, currLp)
            updateActionLpView()
            logLp(player.name, currLp - previousLp, currLp, timer.formattedRemainingTime)
        }
    }

    fun onSubtractClicked(player: Player) {
        _actionLp.value?.let { lp ->
            val previousLp = calculator.getPlayerLifePoint(player)
            if (halve) {
                calculator.subtractLp(player, previousLp / 2)
                halve = false
            } else {
                calculator.subtractLp(player, lp.second)
            }
            val currLp = calculator.getPlayerLifePoint(player)
            updatePlayerLpView(player, previousLp, currLp)
            updateActionLpView()
            logLp(player.name, currLp - previousLp, currLp, timer.formattedRemainingTime)
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
        log.reset()
        timer.cancel()
        updatePlayerLpView(Player.ONE, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
        updatePlayerLpView(Player.TWO, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
    }

    fun onTimerClicked() {
        if (timer.isRunning) timer.pause() else timer.start()
    }

    fun onHalveClicked() {
        halve = true
        onClearClicked()
    }

    private fun updatePlayerLpView(player: Player, previousLp: Int, currLp: Int) {
        if (player == Player.ONE) _playerOneLp.value = Pair(previousLp, currLp)
        else _playerTwoLp.value = Pair(previousLp, currLp)
    }

    private fun updateActionLpView() {
        val previousActionLp = if (_actionLp.value?.second == null) 0 else _actionLp.value?.second!!
        _actionLp.value = Pair(previousActionLp, 0)
    }

    private fun logLp(player: String, actionLp: Int, totalLp: Int, time: String) {
        if (actionLp != 0) {
            log.add(LifePointLogItem(player, actionLp, totalLp, time))
            _actionLpHint.value = log.getLatestEntry()
        }
    }

    enum class CalculatorInput {
        NORMAL,
        ACCUMULATED
    }

    fun onResume() {
        animate = true
    }

    fun onPause() {
        animate = false
    }

}