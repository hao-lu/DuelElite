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
        const val BASE_URL = "https://yugioh.wikia.com/wiki/"

        private val CARD_DETAIL_TYPES = setOf(
            "Card type",
            "Property",
            "Attribute",
            "Types",
            "Rank",
            "Level",
            "Link Arrows",
            "Pendulum Scale",
            "ATK / DEF",
            "ATK / LINK",
            "Ritual Spell Card required",
            "Ritual Monster required",
            "Fusion Material",
            "Materials",
            "Card effect types",
            "Statuses",
            "Description")

//        val DETAILS_MAP = mutableMapOf<String, String>(
//            "Card Type" to "",
//            "Property" to "",
//            "Attribute" to "",
//            "Types" to "",
//            "Rank" to "",
//            "Level" to "",
//            "Link Arrows" to "",
//            "Pendulum Scale" to "",
//            "ATK / DEF" to "",
//            "ATK / LINK" to "",
//            "Ritual Spell Card required" to "",
//            "Ritual Monster required" to "",
//            "Fusion Material" to "",
//            "Materials" to "",
//            "Card effect types" to "",
//            "Statuses" to "",
//            "Description" to ""
//        )


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

    suspend fun fetchCardDetails(cardName: String): List<Pair<String, String>> {
        val document = fetchDocument(generateCardWikiaEndpoint(cardName))
        // <table class = cardtable>
        val cardTable: Element = document.getElementsByClass("cardtable").first()

        var mCardDetailsList: MutableList<Pair<String, String>> = mutableListOf()
        val cardTableRows = cardTable.getElementsByClass("cardtablerow")
        var diffStatuses = 0
        for (tr in cardTableRows) {
            val header = tr.select("th").text()
            val value = tr.select("td").text()

            // Different status for formats TCG, OCG so add newline
            if (header == "Statuses" && tr.select("th").attr("rowspan") != "")
                diffStatuses = tr.select("th").attr("rowspan").toString().toInt()

            if (diffStatuses-- > 0) {
                for (l in mCardDetailsList) {
                    // pair is val can't change so just delete last item and add new with appended data
                    if (l.first == "Statuses") {
                        val statusUpdated = l.second + "\n" + value
                        mCardDetailsList.removeAt(mCardDetailsList.lastIndex)
                        mCardDetailsList.add(Pair("Statuses", statusUpdated))
                    }
                }
            }

            // Get the card descriptions
            if (tr.select("td").select("b").text() == "Card descriptions") {
                val desc = tr.select("table")[1].select("tr")[2].text()
                mCardDetailsList.add(Pair("Description", desc))
            }

            // Formatted Card effects types
            if (header == "Card effect types") {
                val split = value.split(Regex(" "))
                var formatted = split[0]
                for (s in 1 until split.size) {
                    if (split[s][0] == split[s][0].toLowerCase()) formatted += " " + split[s]
                    else formatted += "\n" + split[s]
                }
                if (CARD_DETAIL_TYPES.contains(header)) {
                    mCardDetailsList.add(Pair(header, formatted))
                }
            }
            else {
                if (CARD_DETAIL_TYPES.contains(header)) {
                    mCardDetailsList.add(Pair(header, value))
                }
            }
        }

        return mCardDetailsList
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