package com.lucidity.haolu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchHomeViewModel : ViewModel() {

    private val _searchBar = MutableLiveData<Event<Unit>>()

    val searchBar: LiveData<Event<Unit>> = _searchBar

    fun onSearchBarClicked() {
        _searchBar.value = Event(Unit)
    }

}