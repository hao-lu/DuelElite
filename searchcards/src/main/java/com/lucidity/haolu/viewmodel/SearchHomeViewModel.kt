package com.lucidity.haolu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.Event

class SearchHomeViewModel : ViewModel() {

    private val _searchBar = MutableLiveData<Event<Unit>>()

    val searchBar: LiveData<Event<Unit>> = _searchBar

    fun onSearchBarClick() {
        _searchBar.value = Event(Unit)
    }

}