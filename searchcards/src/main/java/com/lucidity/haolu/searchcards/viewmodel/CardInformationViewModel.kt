package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.Event
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class CardInformationViewModel : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    private val _progressBarEvent = MutableLiveData<Event<Boolean>>()
    private val _cardDetails = MutableLiveData<List<Pair<String, String>>>()

    val progressBarEvent: LiveData<Event<Boolean>> = _progressBarEvent
    val cardDetails: LiveData<List<Pair<String, String>>> = _cardDetails

    suspend fun fetchCardDetails(cardName: String) {
        withContext(Dispatchers.Main) {
            _progressBarEvent.value = Event(true)
            val deferred = async(Dispatchers.IO) {
                yugiohWikiaDataProvider.fetchCardDetails(cardName)
            }
            val details = deferred.await()
            details?.run {
                _cardDetails.value = details
            }
            _progressBarEvent.value = Event(false)
        }
    }


}