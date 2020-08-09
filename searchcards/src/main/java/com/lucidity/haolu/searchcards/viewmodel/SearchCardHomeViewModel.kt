package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.Event

class SearchCardHomeViewModel : ViewModel() {

    private val _searchBar = MutableLiveData<Event<Unit>>()

    val searchBar: LiveData<Event<Unit>> = _searchBar

    fun onSearchBarClick() {
        _searchBar.value = Event(Unit)
    }

}