package com.lucidity.haolu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.Event

class SearchViewModel : ViewModel() {

    private val _backButton = MutableLiveData<Event<Unit>>()

    val backButton: LiveData<Event<Unit>> = _backButton

    fun onBackButtonClick() {
        _backButton.value = Event(Unit)
    }
}