package com.lucidity.haolu.searchcards.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class YugiohWikiaDataProvider {

    companion object {
        const val BASE_URL  = "https://yugioh.wikia.com/wiki/"
    }

    suspend fun fetchCardImageUrl(cardName: String): String {
        val document = fetchDocument(generateCardWikiaEndpoint(cardName))
        // <table class = cardtable>
        val cardTable: Element = document.getElementsByClass("cardtable").first()
        // <a href = ... >
        return cardTable.select("tr")[1]
            .getElementsByClass("cardtable-cardimage")[0]
            .select("a[href]")[0]
            .attr("href")
            .toString()
    }

    private suspend fun fetchDocument(url: String): Document {
        return coroutineScope {
            val deferred = async(Dispatchers.IO) {
                Jsoup.connect(url).get()
            }
            try {
                deferred.await()
            } catch (httpStatusException: HttpStatusException) {
                throw httpStatusException
            } catch (unknownHostException: UnknownHostException) {
                throw unknownHostException
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun generateCardWikiaEndpoint(cardName: String): String {
        return BASE_URL + encodeToHtmlAndReplacePlusWithUnderscore(cardName)
    }

    private fun encodeToHtmlAndReplacePlusWithUnderscore(s: String): String {
        val path = URLEncoder.encode(s, "UTF-8")
        return path.replace("+", "_")
    }
}