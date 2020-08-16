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

//    suspend fun fetchCardImageUrl(url: String): String {
//        return coroutineScope {
//            val deferred = async(Dispatchers.IO) {
//                val document = Jsoup.connect(url).get()
//                // <table class = cardtable>
//                val cardTable: Element = document.getElementsByClass("cardtable").first()
//                // <a href = ... >
//                val imageUrl =
//                    cardTable.select("tr")[1]
//                        .getElementsByClass("cardtable-cardimage")[0]
//                        .select("a[href]")[0]
//                        .attr("href")
//                        .toString()
//                imageUrl
//            }
//            try {
//                deferred.await()
//            } catch (httpStatusException: HttpStatusException) {
//                httpStatusException.toString()
//            } catch (unknownHostException: UnknownHostException) {
//                unknownHostException.toString()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                e.toString()
//            }
//        }
//    }


}
