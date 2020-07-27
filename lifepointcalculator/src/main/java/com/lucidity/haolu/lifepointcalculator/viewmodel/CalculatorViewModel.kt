package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.lifepointcalculator.Event
import com.lucidity.haolu.lifepointcalculator.util.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.R
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

    // TODO: use peek
    private val _playerOneLp = MutableLiveData<Int>()
    private val _playerTwoLp = MutableLiveData<Int>()
    private val _actionLp = MutableLiveData<String>()
    private val _actionLpHint = MutableLiveData<String>()
    private val _playerOneLpIndicatorInvisibility = MutableLiveData<Boolean>()
    private val _playerTwoLpIndicatorInvisibility = MutableLiveData<Boolean>()
    private val _lpIndicatorDrawableId = MutableLiveData<Int>()
    private val _showResetSnackbar = MutableLiveData<Event<String>>()
    private val _animatePlayerOneLp = MutableLiveData<Event<Pair<Int, Int>>>()
    private val _animatePlayerTwoLp = MutableLiveData<Event<Pair<Int, Int>>>()
    private val _animateActionLp = MutableLiveData<Event<Pair<Int, Int>>>()

    val playerOneLp: LiveData<Int> = _playerOneLp
    val playerTwoLp: LiveData<Int> = _playerTwoLp
    val actionLp: LiveData<String> = _actionLp
    val actionLpHint: LiveData<String> = _actionLpHint
    val playerOneLpIndicatorInvisibility: LiveData<Boolean> = _playerOneLpIndicatorInvisibility
    val playerTwoLpIndicatorInvisibility: LiveData<Boolean> = _playerTwoLpIndicatorInvisibility
    val lpIndicatorDrawableId: LiveData<Int> = _lpIndicatorDrawableId
    val showResetSnackbar: LiveData<Event<String>> = _showResetSnackbar
    val animatePlayerOneLp: LiveData<Event<Pair<Int, Int>>> = _animatePlayerOneLp
    val animatePlayerTwoLp: LiveData<Event<Pair<Int, Int>>> = _animatePlayerTwoLp
    val animateActionLp: LiveData<Event<Pair<Int, Int>>> = _animateActionLp

    private var halve = false
    var inputType = R.layout.layout_accumulated_input

    var playerOneLpBarInvisible = false
    var playerTwoLpBarInvisible = false

    fun onNumberClicked(num: String) {
        halve = false
        setAllIndicatorInvisible()
        when (inputType) {
            R.layout.layout_normal_input -> {
                val appendNum = appendNum(_actionLp.value?.toIntOrNull(), num)
                if (isLessThanMaxLifePointOrNull(appendNum)) {
                    updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, appendNum)
                }
            }
            R.layout.layout_accumulated_input -> {
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
        if (currLp == 0) {
            _showResetSnackbar.value = Event(String.format(snackbarMessage, player.name))
            timer.cancel()
        }
    }

    private fun isHalveOrNull(resourceId: Int?) =
        resourceId == R.string.halve || resourceId == null

    fun onClearClicked() {
        halve = false
        updateActionLp(0, 0)
        log.getLatestEntry()?.let { logItem ->
            setIndicatorInvisibility(logItem.player)
        }
    }

    fun reset() {
        calculator.reset()
        log.reset()
        timer.cancel()
        playerOneLpBarInvisible = false
        playerTwoLpBarInvisible = false
        _playerOneLpIndicatorInvisibility.value = true
        _playerTwoLpIndicatorInvisibility.value = true
        _actionLp.value = ""
        _actionLpHint.value = "0000"
        _playerOneLp.value = LifePointCalculator.START_LP
        _playerTwoLp.value = LifePointCalculator.START_LP

//        updatePlayerLp(Player.ONE, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
//        updatePlayerLp(Player.TWO, LifePointCalculator.START_LP, LifePointCalculator.START_LP)
    }

    fun onTimerClicked() {
        if (timer.isRunning) timer.pause() else timer.start()
    }

    fun onHalveClicked() {
        halve = true
        setAllIndicatorInvisible()
        _actionLp.value = halveText
    }

    private fun updatePlayerLp(player: Player, previousLp: Int, currLp: Int) {
        if (player == Player.ONE) {
            _playerOneLp.value = currLp
            _animatePlayerOneLp.value = Event(Pair(previousLp, currLp))
        }
        else {
            _playerTwoLp.value = currLp
            _animatePlayerTwoLp.value = Event(Pair(previousLp, currLp))
        }
    }

    private fun logLp(player: String, actionLp: Int, totalLp: Int, time: String) {
        if (actionLp != 0) {
            log.add(LifePointLogItem(player, actionLp, totalLp, time))
            _actionLpHint.value = abs(actionLp).toString()
            _lpIndicatorDrawableId.value = if (actionLp < 0) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up
            setIndicatorInvisibility(player)
        }
    }

    private fun updateActionLp(prevLp: Int, currLp: Int) {
        _actionLp.value = if (currLp == 0) null else currLp.toString()
        if (prevLp != currLp && inputType == R.layout.layout_accumulated_input) _animateActionLp.value = Event(Pair(prevLp, currLp))
    }

    private fun setIndicatorInvisibility(player: String) {
        if (player == Player.ONE.name) {
            _playerOneLpIndicatorInvisibility.value = false
            _playerTwoLpIndicatorInvisibility.value = true
        } else {
            _playerOneLpIndicatorInvisibility.value = true
            _playerTwoLpIndicatorInvisibility.value = false
        }
    }

    private fun setAllIndicatorInvisible() {
        _playerOneLpIndicatorInvisibility.value = true
        _playerTwoLpIndicatorInvisibility.value = true
    }

    enum class CalculatorInput {
        NORMAL,
        ACCUMULATED
    }
}