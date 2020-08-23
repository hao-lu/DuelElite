package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.Event
import com.lucidity.haolu.searchcards.SearchCardsDatabase
import com.lucidity.haolu.searchcards.room.entity.RecentSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchCardHomeViewModel : ViewModel() {

    // TODO: make a repository layer when add caching
    private val db = SearchCardsDatabase.instance

    private val _searchBar = MutableLiveData<Event<Unit>>()
    private val _recentSearchList = MutableLiveData<List<RecentSearch>>()

    val searchBar: LiveData<Event<Unit>> = _searchBar
    val recentSearchList: LiveData<List<RecentSearch>> = _recentSearchList

    fun onSearchBarClick() {
        _searchBar.value = Event(Unit)
    }

    suspend fun updateRecentSearchList() {
        withContext(Dispatchers.Main){
            val deferred = async(Dispatchers.IO) {
                db?.recentSearchDao()?.getTop5RecentSearch()
            }
            val list = deferred.await()
            _recentSearchList.value = list
        }
    }
}