package com.lucidity.haolu.lifepointcalculator

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.TimeUnit

class PausableCountDownTimer(
    private val startTime: Long = 2400000L,
    private val intervalTime: Long = 1000L
) {
    private lateinit var timer: CountDownTimer
    private val _duelTime: MutableLiveData<String> = MutableLiveData()
    val duelTime: LiveData<String> = _duelTime
    var isRunning: Boolean = false
        private set
    var remainingTime: Long = startTime
        private set
    var formattedRemainingTime: String = formatTimeToHourMinute(startTime)
        private set

    fun start() {
        if (!isRunning) {
            timer = object : CountDownTimer(remainingTime, intervalTime) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTime = millisUntilFinished
                    _duelTime.value = formatTimeToHourMinute(millisUntilFinished)
                    formattedRemainingTime = _duelTime.value.toString()
                }

                override fun onFinish() {
                    _duelTime.value = "Round Over"
                }
            }.start()
            isRunning = true
        }
    }

    fun pause() {
        if (isRunning) {
            timer.cancel()
            isRunning = false
        }
    }

    fun cancel() {
        if (isRunning) {
            pause()
            remainingTime = startTime
        }
    }

    private fun formatTimeToHourMinute(millisUntilFinished: Long): String =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished
                        )
                    )
        )
}