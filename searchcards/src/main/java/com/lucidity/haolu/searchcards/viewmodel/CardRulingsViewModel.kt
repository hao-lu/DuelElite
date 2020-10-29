package com.lucidity.haolu.searchcards.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.base.Event
import com.lucidity.haolu.searchcards.model.CardRulings
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class CardRulingsViewModel : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    private val _progressBarEvent = MutableLiveData<Event<Boolean>>()
    private val _cardRulings = MutableLiveData<ArrayList<CardRulings>>()
    private val _emptyCardRulingsVisibility = MutableLiveData<Int>()

    val progressBarEvent: LiveData<Event<Boolean>> = _progressBarEvent
    val cardRulings: LiveData<ArrayList<CardRulings>> = _cardRulings
    val emptyStateCardRulingsVisibility: LiveData<Int> = _emptyCardRulingsVisibility

    suspend fun fetchCardRulings(cardName: String) {
        withContext(Dispatchers.Main) {
            _progressBarEvent.value = Event(true)
            val deferred = async(Dispatchers.IO) {
                yugiohWikiaDataProvider.fetchCardRulings(cardName)
            }
            val rulings = deferred.await()
            _progressBarEvent.value = Event(false)
            if (rulings == null) {
                _emptyCardRulingsVisibility.value = View.VISIBLE
            } else {
                _emptyCardRulingsVisibility.value = View.GONE
                _cardRulings.value = rulings
            }
        }
    }
}