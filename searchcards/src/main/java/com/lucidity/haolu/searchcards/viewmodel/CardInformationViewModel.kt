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
            information?.run {
                _cardInformation.value = information
            }
            _progressBarEvent.value = Event(false)
        }
    }


}