package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardInformationViewModel : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    val _cardDetails = MutableLiveData<List<Pair<String, String>>>()
    val cardDetails: LiveData<List<Pair<String, String>>> = _cardDetails

    suspend fun fetchCardDetails(cardName: String) {
        withContext(Dispatchers.Main) {
            _cardDetails.value = yugiohWikiaDataProvider.fetchCardDetails(cardName)
        }
    }


}