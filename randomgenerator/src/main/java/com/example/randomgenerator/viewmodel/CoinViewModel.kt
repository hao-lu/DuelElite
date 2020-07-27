package com.example.randomgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.randomgenerator.Event
import com.example.randomgenerator.R
import com.example.randomgenerator.model.RandomGenerator

class CoinViewModel : ViewModel() {

    private val _flipCoin = MutableLiveData<Event<Boolean>>()

    val flipCoin: LiveData<Event<Boolean>> = _flipCoin

    fun onCoinClicked() {
        _flipCoin.value = Event(RandomGenerator.isHead())
    }

}