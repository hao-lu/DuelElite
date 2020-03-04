package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.util.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.SingleLiveEvent
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem
import com.lucidity.haolu.lifepointcalculator.model.LifePointLog
import com.lucidity.haolu.lifepointcalculator.model.Player
import kotlin.math.abs

class CalculatorViewModel : ViewModel() {

    private val calculator = LifePointCalculator()
    val log: LifePointLog = LifePointLog(mutableListOf())
    val timer = PausableCountDownTimer()

    private val halveText = "HALVE"
    private val snackbarMessage = "%s has won"

    private val _playerOneLp: MutableLiveData<Int> = MutableLiveData()
    private val _playerTwoLp: MutableLiveData<Int> = MutableLiveData()
    private val _actionLp: MutableLiveData<String> = MutableLiveData()
    private val _actionLpHint: MutableLiveData<String> = MutableLiveData()
    private val _playerOneLpIndicatorInvisible: MutableLiveData<Boolean> = MutableLiveData()
    private val _playerTwoLpIndicatorInvisible: MutableLiveData<Boolean> = MutableLiveData()
    private val _lpIndicatorDrawableId: MutableLiveData<Int> = MutableLiveData()
    private val _showResetSnackbar: SingleLiveEvent<String> = SingleLiveEvent()
    private val _animatePlayerOneLp: SingleLiveEvent<Pair<Int, Int>> = SingleLiveEvent()
    private val _animatePlayerTwoLp: SingleLiveEvent<Pair<Int, Int>> = SingleLiveEvent()
    private val _animateActionLp: SingleLiveEvent<Pair<Int, Int>> = SingleLiveEvent()

    val playerOneLp: LiveData<Int> = _playerOneLp
    val playerTwoLp: LiveData<Int> = _playerTwoLp
    val actionLp: LiveData<String> = _actionLp
    val actionLpHint: LiveData<String> = _actionLpHint
    val playerOneLpIndicatorInvisible: LiveData<Boolean> = _playerOneLpIndicatorInvisible
    val playerTwoLpIndicatorInvisible: LiveData<Boolean> = _playerTwoLpIndicatorInvisible
    val lpIndicatorDrawableId: LiveData<Int> = _lpIndicatorDrawableId
    val showResetSnackbar: LiveData<String> = _showResetSnackbar
    val animatePlayerOneLp: LiveData<Pair<Int, Int>> = _animatePlayerOneLp
    val animatePlayerTwoLp: LiveData<Pair<Int, Int>> = _animatePlayerTwoLp
    val animateActionLp: LiveData<Pair<Int, Int>> = _animateActionLp

    private var halve = false
    var inputType = CalculatorInput.ACCUMULATED

    var playerOneLpBarInvisible = false
    var playerTwoLpBarInvisible = false

    fun onNumberClicked(num: String) {
        halve = false
        when (inputType) {
            CalculatorInput.NORMAL -> {
                val appendNum = appendNum(_actionLp.value?.toIntOrNull(), num)
                if (isLessThanMaxLifePointOrNull(appendNum)) {
//                    updateActionLp(appendNum, appendNum)
                    updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, appendNum)
                }
            }
            CalculatorInput.ACCUMULATED -> {
                val sumNum = sumNum(_actionLp.value?.toIntOrNull(), num)
                if (isLessThanMaxLifePointOrNull(sumNum)) {
                    updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, sumNum)
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
            val prevLp = calculator.getPlayerLifePoint(player)
            calculator.addLp(player, lp.toInt())
            val currLp = calculator.getPlayerLifePoint(player)
            updateViews(player, prevLp, currLp)
        }
    }

    fun onSubtractClicked(player: Player) {
        _actionLp.value?.let { lp ->
            val prevLp = calculator.getPlayerLifePoint(player)
            if (halve) {
                calculator.subtractLp(player, prevLp / 2)
                halve = false
            } else {
                calculator.subtractLp(player, lp.toInt())
            }
            val currLp = calculator.getPlayerLifePoint(player)
            updateViews(player, prevLp, currLp)
        }
    }

    private fun updateViews(player: Player, prevLp: Int, currLp: Int) {
        updatePlayerLp(player, prevLp, currLp)
        updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, 0)
        logLp(player.name, currLp - prevLp, currLp, timer.formattedRemainingTime)
        if (currLp == 0) _showResetSnackbar.value = String.format(snackbarMessage, player.name)
    }

    private fun isHalveOrNull(resourceId: Int?) =
        resourceId == R.string.halve || resourceId == null

    fun onClearClicked() {
        halve = false
        updateActionLp(0, 0)
    }

    fun reset() {
        calculator.reset()
        log.reset()
        timer.cancel()
        _playerOneLpIndicatorInvisible.value = true
        _playerTwoLpIndicatorInvisible.value = true
        _actionLp.value = ""
        _actionLpHint.value = "0000"
        _playerOneLp.value = 8000
        _playerTwoLp.value = 8000
//        updatePlayerLp(Player.ONE, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
//        updatePlayerLp(Player.TWO, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
    }

    fun onTimerClicked() {
        if (timer.isRunning) timer.pause() else timer.start()
    }

    fun onHalveClicked() {
        halve = true
        _actionLp.value = halveText
    }

    private fun updatePlayerLp(player: Player, previousLp: Int, currLp: Int) {
        if (player == Player.ONE) {
            _playerOneLp.value = currLp
            _animatePlayerOneLp.value = Pair(previousLp, currLp)
        }
        else {
            _playerTwoLp.value = currLp
            _animatePlayerTwoLp.value = Pair(previousLp, currLp)
        }
    }

    private fun logLp(player: String, actionLp: Int, totalLp: Int, time: String) {
        if (actionLp != 0) {
            log.add(LifePointLogItem(player, actionLp, totalLp, time))
            _actionLpHint.value = abs(actionLp).toString()
            _lpIndicatorDrawableId.value = if (actionLp < 0) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up
            setIndicatorInvisible(player)
        }
    }

    private fun updateActionLp(prevLp: Int, currLp: Int) {
        _actionLp.value = if (currLp == 0) null else currLp.toString()
        if (prevLp != currLp && inputType == CalculatorInput.ACCUMULATED) _animateActionLp.value = Pair(prevLp, currLp)
    }

    private fun setIndicatorInvisible(player: String) {
        if (player == Player.ONE.name) {
            _playerOneLpIndicatorInvisible.value = false
            _playerTwoLpIndicatorInvisible.value = true
        } else {
            _playerOneLpIndicatorInvisible.value = true
            _playerTwoLpIndicatorInvisible.value = false
        }
    }

    enum class CalculatorInput {
        NORMAL,
        ACCUMULATED
    }
}