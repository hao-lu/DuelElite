package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.searchcards.network.YugiohWikiaDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchCardDetailsViewModel(val cardName: String) : ViewModel() {

    private val yugiohWikiaDataProvider = YugiohWikiaDataProvider()

    private val scope = CoroutineScope(Dispatchers.IO)

    val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    suspend fun fetchCardImageUrl(cardName: String): String {
        withContext(Dispatchers.Main) {
            _imageUrl.value = yugiohWikiaDataProvider.fetchCardImageUrl(cardName)
        }
        return yugiohWikiaDataProvider.fetchCardImageUrl(cardName)
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
