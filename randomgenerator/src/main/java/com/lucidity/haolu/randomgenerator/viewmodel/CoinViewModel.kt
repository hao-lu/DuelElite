package com.lucidity.haolu.randomgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.randomgenerator.Event
import com.lucidity.haolu.randomgenerator.model.RandomGenerator

class CoinViewModel : ViewModel() {

    private val _flipCoin = MutableLiveData<Event<Boolean>>()

    val flipCoin: LiveData<Event<Boolean>> = _flipCoin

    fun onCoinClicked() {
        _flipCoin.value =
            Event(RandomGenerator.isHead())
    }

}