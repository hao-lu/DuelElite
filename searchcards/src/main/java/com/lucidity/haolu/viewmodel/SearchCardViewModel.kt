package com.lucidity.haolu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.Card
import com.lucidity.haolu.Event
import com.lucidity.haolu.SearchCardsDatabase
import kotlinx.coroutines.*

class SearchCardViewModel : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _backButton = MutableLiveData<Event<Unit>>()

    private val db = SearchCardsDatabase.instance

    val backButton: LiveData<Event<Unit>> = _backButton

    private val searchResults = MutableLiveData<List<Card>>()

    private var list: List<Card>? = null

    fun onBackButtonClick() {
        _backButton.value = Event(Unit)
    }

    fun getSearchResults(term: String) {
        scope.async() {
            list = _getSearchResults(term)
//            searchResults.value = deferred.await()
            withContext(Dispatchers.Main) {
                searchResults.value = list
            }
            println(list)
        }
    }

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


}