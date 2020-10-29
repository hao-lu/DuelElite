package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.base.Event
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.SearchCardsDatabase
import kotlinx.coroutines.*

class SearchCardResultsViewModel : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    // TODO: make a repository layer when add caching
    private val db = SearchCardsDatabase.instance

    private val _backButton = MutableLiveData<Event<Unit>>()
    private val _searchResults = MutableLiveData<List<Card>>()

    val backButton: LiveData<Event<Unit>> = _backButton
    val searchResults: LiveData<List<Card>> = _searchResults

    fun onBackButtonClick() {
        _backButton.value = Event(Unit)
    }

    suspend fun getSearchResult(term: String) {
        withContext(Dispatchers.Main) {
            if (term.isEmpty()) {
                _searchResults.value = emptyList()
            } else {
                val newTerm = term.replace(" ", "%")
                val deferred = async(Dispatchers.IO) {
                    db?.cardDao()?.getAllContainsSubstring(newTerm)
                }
                _searchResults.value = deferred.await()
            }
        }
    }

    private fun replaceSpaceWithLikeOperator(term: String): String {
        return term.replace(" ", "%")
    }

}