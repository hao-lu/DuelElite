package com.example.haolu.duelmaster

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    var mCardDetailsList: MutableList<Pair<String, String>> = mutableListOf()

    private val mCardDetailTypes = arrayOf(
            "Card type",
            "Property",
            "Attribute",
            "Types",
            "Level",
            "Pendulum Scale",
            "ATK / DEF",
            "Materials",
            "Card effect mCardDetailTypes",
            "Statuses",
            "Description",
            "ImageUrl")

    private val TAG = "DetailsFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_details, container, false)
//        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
//        val rootView = binding.root

        val cardName = arguments.getString("cardName")

        Log.d(TAG, cardName)
//
//        binding.setVariable(BR.CardDetails, mCardDetails)

        ParseDetailsTask().execute(cardName)


        return rootView
    }

    private inner class ParseDetailsTask : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseDetailsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/"

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val cardNamePath = cardName?.replace(" ", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {

                Log.d(TAG, "QUERY : " + cardName?.replace(" ", "_"))

                val document = Jsoup.connect(cardUrl).get()
                // <table class = cardtable>
                val cardTable: Element = document.select("table").first()

                // <a href = ... >
                val imageUrl = cardTable.select("tr")[1].
                        getElementsByClass("cardtable-cardimage")[0].
                        select("a[href]")[0].
                        attr("href").toString()

                mCardDetailsList.add(Pair("ImageUrl", imageUrl))

//                // Get the header
                val cardTableRows = cardTable.getElementsByClass("cardtablerow")
                var diffStatuses = 0
                for (e in cardTableRows) {
                    val header = e.select("th").text()
                    val value = e.select("td").text()

                    // Different status for formats TCG, OCG
                    if (header == "Statuses" && e.select("th").attr("rowspan") != "")
                        diffStatuses = e.select("th").attr("rowspan").toString().toInt()

                    if (diffStatuses-- > 0) {
                        for (l in mCardDetailsList) {
                            if (l.first == "Statuses") {
                                val statusUpdated = l.second + value
                                mCardDetailsList.removeAt(mCardDetailsList.lastIndex)
                                mCardDetailsList.add(Pair("Statuses", statusUpdated))
                            }
                        }
                    }

                    addData(Pair(header, value))
                }

                // <td class = "navbox-mCardDetailsList" ... >
//                val description = cardTable.getElementsByClass("cardtablerow")[19].select("table")[1].select("tr")[2].text()
////                Log.d(TAG, description)
//                mCardDetails.update("Description", description)

//                mCardDetails.print()
                print()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            // no internet connection error
            // no webpage error
            return null
        }

        override fun onPostExecute(result: Void?) {
//            super.onPostExecute(result)
            val row = View.inflate(context, R.layout.fragment_table_row, null)
            val cardHeader = row.findViewById(R.id.card_header) as TextView
            val cardValue = row.findViewById(R.id.card_value) as TextView
            cardHeader.text = "HELLO"
            cardValue.text = "WOLRD"
            val row2 = View.inflate(context, R.layout.fragment_table_row, null)
            var cardHeader2 = row2.findViewById(R.id.card_header) as TextView
            val cardValue2 = row2.findViewById(R.id.card_value) as TextView
            cardHeader2.text = "BYE"
            cardValue2.text = "WOLRD"
            card_information.addView(row)
            card_information.addView(row2)
        }
    }

    fun addData(p: Pair<String, String>) {
        var pair = p
        for (s in mCardDetailTypes) {
            if (p.first == s) mCardDetailsList.add(pair)
        }
    }

    fun print() {
        for (s in mCardDetailsList) {
            println(s)
        }
    }
}