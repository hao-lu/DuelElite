package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class SearchCardDetailsViewModel(val cardName: String?) : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    suspend fun fetchCardImageUrl(cardName: String) {
        withContext(Dispatchers.Main) {
            val deferred = async(Dispatchers.IO) {
                yugiohWikiaDataProvider.fetchCardImageUrl(cardName)
            }
            val url = deferred.await()
            if (url != null) {
                _imageUrl.value = deferred.await()
            }
        }
    }
}
