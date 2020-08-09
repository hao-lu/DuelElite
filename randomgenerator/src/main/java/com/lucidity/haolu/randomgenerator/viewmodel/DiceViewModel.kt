package com.lucidity.haolu.randomgenerator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.randomgenerator.Event
import com.lucidity.haolu.randomgenerator.model.RandomGenerator

class DiceViewModel : ViewModel() {

    private val _rollDice = MutableLiveData<Event<Int>>()

    val rollDice: LiveData<Event<Int>> = _rollDice

    fun onDiceRolled() {
        _rollDice.value =
            Event(RandomGenerator.roll())
    }

}