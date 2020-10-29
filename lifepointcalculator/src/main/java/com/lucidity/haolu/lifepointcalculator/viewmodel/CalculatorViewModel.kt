package com.lucidity.haolu.lifepointcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.base.Event
import com.lucidity.haolu.lifepointcalculator.util.PausableCountDownTimer
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.model.*
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
    private val _showInputTypeBottomSheet = MutableLiveData<Event<Unit>>()
    private val _showNormalInput = MutableLiveData<Event<Unit>>()
    private val _showAccumulatedInput = MutableLiveData<Event<Unit>>()
    private val _navigateToLogEvent = MutableLiveData<Event<Unit>>()
    private val _duelTimeButtonInvisibility = MutableLiveData<Boolean>()
    private val _duelTimeTextViewInvisibility = MutableLiveData<Boolean>()
    private val _resetClickEvent = MutableLiveData<Event<Unit>>()

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
    val showInputTypeBottomSheet: LiveData<Event<Unit>> = _showInputTypeBottomSheet
    val showNormalInput: LiveData<Event<Unit>> = _showNormalInput
    val showAccumulatedInput: LiveData<Event<Unit>> = _showAccumulatedInput
    val navigateToLogEvent: LiveData<Event<Unit>> = _navigateToLogEvent
    val duelTimeButtonInvisibility: LiveData<Boolean> = _duelTimeButtonInvisibility
    val duelTimeTextViewInvisibility: LiveData<Boolean> = _duelTimeTextViewInvisibility
    val resetClickEvent: LiveData<Event<Unit>> = _resetClickEvent

    private var halve = false
    private var inputType = CalculatorInputType.NORMAL.name

    var playerOneLpBarInvisible = false
    var playerTwoLpBarInvisible = false

    fun onNumberClicked(num: String) {
        halve = false
        setAllIndicatorInvisible()
        when (inputType) {
            CalculatorInputType.NORMAL.name -> {
                val appendNum = appendNum(_actionLp.value?.toIntOrNull(), num)
                if (isLessThanMaxLifePointOrNull(appendNum)) {
                    updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, appendNum)
                }
            }
            CalculatorInputType.ACCUMULATED.name -> {
                val sumNum = sumNum(_actionLp.value?.toIntOrNull(), num)
                if (isLessThanMaxLifePointOrNull(sumNum)) {
                    updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, sumNum)
                }
            }
        }
    }

    fun initInputView(inputType: String) {
        this.inputType = inputType
        when (inputType) {
            CalculatorInputType.ACCUMULATED.name -> _showAccumulatedInput.value = Event(Unit)
            CalculatorInputType.NORMAL.name -> _showNormalInput.value = Event(Unit)
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

    fun onInputTypeClick() {
        _showInputTypeBottomSheet.value = Event(Unit)
    }

    fun onAccumulatedClick() {
        inputType = CalculatorInputType.ACCUMULATED.name
        _showAccumulatedInput.value = Event(Unit)
    }

    fun onNormalClick() {
        inputType = CalculatorInputType.NORMAL.name
        _showNormalInput.value = Event(Unit)
    }

    fun onLogClick() {
        _navigateToLogEvent.value = Event(Unit)
    }

    private fun updateViews(player: Player, prevLp: Int, currLp: Int) {
        updatePlayerLp(player, prevLp, currLp)
        updateActionLp(_actionLp.value?.toIntOrNull() ?: 0, 0)
        logLp(player.tag, currLp - prevLp, currLp, timer.formattedRemainingTime)
        if (currLp == 0) {
            _showResetSnackbar.value = Event(String.format(snackbarMessage, determineWinnerString(player)))
            timer.cancel()
        }
    }

    private fun determineWinnerString(player: Player): String {
        return if (player.tag == Player.ONE.tag) Player.TWO.tag else Player.ONE.tag
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
        _duelTimeButtonInvisibility.value = false
        _duelTimeTextViewInvisibility.value = true

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

    fun onResetClick() {
        _resetClickEvent.value = Event(Unit)
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
        if (prevLp != currLp && inputType == CalculatorInputType.ACCUMULATED.name) _animateActionLp.value = Event(Pair(prevLp, currLp))
    }

    private fun setIndicatorInvisibility(player: String) {
        if (player == Player.ONE.tag) {
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

}