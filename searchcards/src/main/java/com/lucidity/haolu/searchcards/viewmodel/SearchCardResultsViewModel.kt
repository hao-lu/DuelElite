package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.Event
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

//    fun getSearchResults(term: String) {
//        scope.async() {
//            list = _getSearchResults(term)
////            searchResults.value = deferred.await()
//            withContext(Dispatchers.Main) {
//                searchResults.value = list
//            }
//        }
//    }

    private suspend fun _getSearchResults(term: String): List<Card>? {
        val newTerm = term.replace(" ", "%")
        return db?.cardDao()?.getAllContainsSubstring(newTerm)
    }

    suspend fun getSearchResult(term: String): List<Card>? =
        coroutineScope {
            val newTerm = term.replace(" ", "%")
            val deferred = async(Dispatchers.IO) {
                db?.cardDao()?.getAllContainsSubstring(newTerm)
            }
            deferred.await()
        }


    fun getSearchResultsNew(searchTerm: String) {
        viewModelScope.launch {
            val deferred = async(Dispatchers.IO) {
                val newTerm = replaceSpaceWithLikeOperator(searchTerm)
                db?.cardDao()?.getAllContainsSubstring(newTerm)
            }
            deferred.await()
        }
    }

    private fun replaceSpaceWithLikeOperator(term: String): String {
        return term.replace(" ", "%")
    }

}