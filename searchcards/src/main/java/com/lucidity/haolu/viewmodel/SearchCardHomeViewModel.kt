package com.lucidity.haolu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.Event
import com.lucidity.haolu.SearchCardsDatabase
import java.io.IOException

class SearchCardHomeViewModel : ViewModel() {

    private val _searchBar = MutableLiveData<Event<Unit>>()

    val searchBar: LiveData<Event<Unit>> = _searchBar

    fun onSearchBarClick() {
        _searchBar.value = Event(Unit)
    }

}