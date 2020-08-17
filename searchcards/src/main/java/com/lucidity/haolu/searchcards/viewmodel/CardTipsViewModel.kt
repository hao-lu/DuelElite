package com.lucidity.haolu.searchcards.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.Event
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class CardTipsViewModel : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    private val _progressBarEvent = MutableLiveData<Event<Boolean>>()
    private val _cardTips = MutableLiveData<ArrayList<String>>()
    private val _emptyStateCardTipsVisibility = MutableLiveData<Int>()

    val progressBarEvent: LiveData<Event<Boolean>> = _progressBarEvent
    val cardTips: LiveData<ArrayList<String>> = _cardTips
    val emptyStateCardTipsVisibility: LiveData<Int> = _emptyStateCardTipsVisibility

    suspend fun fetchCardTips(cardName: String) {
        withContext(Dispatchers.Main) {
            _progressBarEvent.value = Event(true)
            val deferred = async(Dispatchers.IO) {
                yugiohWikiaDataProvider.fetchCardTips(cardName)
            }
            val tips = deferred.await()
            _progressBarEvent.value = Event(false)
            if (tips == null) {
                _emptyStateCardTipsVisibility.value = View.VISIBLE
            } else {
                _emptyStateCardTipsVisibility.value = View.GONE
                _cardTips.value = tips
            }
        }
    }
}