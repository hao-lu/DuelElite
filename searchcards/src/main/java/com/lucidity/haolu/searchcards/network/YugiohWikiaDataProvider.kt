package com.lucidity.haolu.searchcards.network

import android.util.Log
import com.lucidity.haolu.searchcards.model.CardRulings
import kotlinx.coroutines.*
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class YugiohWikiaDataProvider {

    private val TAG = "YugiohWikiaDataProvider"

    private val scope = CoroutineScope(Dispatchers.IO + Job())

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
    }

    suspend fun fetchCardImageUrl(cardName: String): String? {
        val document = fetchDocument(generateCardWikiaEndpoint(cardName))
        document?.let {
            // <table class = cardtable>
            val cardTable: Element = document.getElementsByClass("cardtable").first()
            // <a href = ... >
            return cardTable.select("tr")[1]
                .getElementsByClass("cardtable-cardimage")[0]
                .select("a[href]")[0]
                .attr("href")
                .toString()
        }
        return null
    }

    suspend fun fetchCardInformation(cardName: String): List<Pair<String, String>>? {
        val document = fetchDocument(generateCardWikiaEndpoint(cardName))
        document?.let {
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
        return null
    }

    suspend fun fetchCardRulings(cardName: String): ArrayList<CardRulings>? {
        val document = fetchDocument(generateCardWikiaRulingEndpoint(cardName))
        document?.let {
            val listOfCardRulings = ArrayList<CardRulings>()
            val article = document.select("article").first()
            val div = article.getElementById("mw-content-text")
            val children = div.children()
            var addToList = false
            for (child in children) {
                // Add all header sections excluding References and Notes
                if (child.`is`("h2") && child.text() != "References" && child.text() != "Notes") {
                    addToList = true
                    // New header
                    listOfCardRulings.add(CardRulings(arrayListOf()))
                    Log.d(TAG, child.text())
                } else if (child.`is`("h2")) {
                    addToList = false
                }
                if (addToList) {
                    val rulingList = listOfCardRulings[listOfCardRulings.size - 1]
                    listOfCardRulings[listOfCardRulings.size - 1] = addRulingsItemHelper(child, rulingList)
                }
            }
            return listOfCardRulings
        }
        return null
    }

    /**
     * Helper to parse Rulings
     */
    private fun addRulingsItemHelper(parent: Element, rulings: CardRulings): CardRulings {
        // Removes the superscripts (foot notes)
        val text = parent.text().replace(Regex("((References: )*\\[.*?\\])"), "")
        when {
            parent.`is`("h2") -> rulings.addHeader2(text)
            // Subheader
            parent.`is`("h3") -> rulings.addHeader2(text)
            // Div has children (red / green box)
            parent.`is`("div") -> {
                val children = parent.children()
                for (child in children) {
                    addRulingsItemHelper(child, rulings)
                }
            }
            // Item
            parent.`is`("ul") -> rulings.addUl(text)
        }
        return rulings
    }

    suspend fun fetchCardTips(cardName: String): ArrayList<String>? {
        val mTipsList: ArrayList<String> = arrayListOf()
        val document = fetchDocument(generateCardWikiaTipsEndpoint(cardName))
        document?.let {
            val article = document.select("article").first()
            val context = article.getElementById("mw-content-text")
            val children = context.children()
            for (c in children) {
                if (c.text() == "Traditional Format" || c.text() == "List")
                    break
                if (c.`is`("ul")) mTipsList.add(c.text())
            }
            return mTipsList
        }
        return null
    }

    private suspend fun fetchDocument(url: String): Document? {
        return supervisorScope {
            val deferred = async(Dispatchers.IO) {
                Jsoup.connect(url).get()
            }
            try {
                deferred.await()
            } catch (httpStatusException: HttpStatusException) {
                Log.e(TAG, httpStatusException.toString())
                null
            } catch (unknownHostException: UnknownHostException) {
                Log.e(TAG, unknownHostException.toString())
                null
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                null
            }
        }
    }

    private fun generateCardWikiaEndpoint(cardName: String): String {
        return BASE_URL + encodeToHtmlAndReplacePlusWithUnderscore(cardName)
    }

    private fun generateCardWikiaRulingEndpoint(cardName: String): String {
        return BASE_URL + "Card_Rulings:" + encodeToHtmlAndReplacePlusWithUnderscore(cardName)
    }

    private fun generateCardWikiaTipsEndpoint(cardName: String): String {
        return BASE_URL + "Card_Tips:" + encodeToHtmlAndReplacePlusWithUnderscore(cardName)
    }

    private fun encodeToHtmlAndReplacePlusWithUnderscore(s: String): String {
        val path = URLEncoder.encode(s, "UTF-8")
        return path.replace("+", "_")
    }
}