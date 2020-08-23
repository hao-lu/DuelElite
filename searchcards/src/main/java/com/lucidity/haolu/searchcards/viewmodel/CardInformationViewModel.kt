package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.base.Event
import com.lucidity.haolu.searchcards.SearchCardsDatabase
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import com.lucidity.haolu.searchcards.room.entity.CardType
import com.lucidity.haolu.searchcards.room.entity.RecentSearch
import kotlinx.coroutines.*

class CardInformationViewModel : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    // TODO: make a repository layer when add caching
    private val db = SearchCardsDatabase.instance

    private val _progressBarEvent = MutableLiveData<Event<Boolean>>()
    private val _cardInformation = MutableLiveData<List<Pair<String, String>>>()

    val progressBarEvent: LiveData<Event<Boolean>> = _progressBarEvent
    val cardInformation: LiveData<List<Pair<String, String>>> = _cardInformation

    suspend fun fetchCardInformation(cardName: String) {
        withContext(Dispatchers.Main) {
            _progressBarEvent.value = Event(true)
            val deferred = async(Dispatchers.IO) {
                yugiohWikiaDataProvider.fetchCardInformation(cardName)
            }
            val information = deferred.await()
            addToRecentSearchList(cardName, information?.get(0)?.second ?: "")
            information?.run {
                _cardInformation.value = information
            }
            _progressBarEvent.value = Event(false)
        }
    }

    private fun addToRecentSearchList(cardName: String, cardType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.recentSearchDao()
                ?.insert(RecentSearch(cardName, getCardTypeColor(cardType), System.currentTimeMillis()))
            db?.recentSearchDao()?.deleteOldest()
        }
    }

    private fun getCardTypeColor(cardType: String): Int {
        return when (cardType.toUpperCase()) {
            CardType.MONSTER.name -> CardType.MONSTER.color
            CardType.SPELL.name -> CardType.SPELL.color
            CardType.TRAP.name -> CardType.TRAP.color
            else -> CardType.UNKNOWN.color
        }
    }


}