package com.example.haolu.duelmaster

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
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
            "Card effect types",
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

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Details Paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Details Stop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "Details DestoryView")
        mCardDetailsList.clear()
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
                for (tr in cardTableRows) {
                    val header = tr.select("th").text()
                    val value = tr.select("td").text()

                    // Different status for formats TCG, OCG
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

                    addData(Pair(header, value))
                }
//                print()
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
            for (detail in mCardDetailsList) {
                if (detail.first != "ImageUrl") {
                    val row = View.inflate(context, R.layout.fragment_table_row, null)
                    val cardHeader = row.findViewById(R.id.card_header) as TextView
                    val cardValue = row.findViewById(R.id.card_value) as TextView
                    cardHeader.text = detail.first
                    cardValue.text = detail.second
                    card_information.addView(row)
                }
            }

            val image = activity.findViewById(R.id.header) as ImageView
            Picasso.with(context).load(mCardDetailsList[0].second).into(image)
        }
    }

    fun addData(p: Pair<String, String>) {
        for (s in mCardDetailTypes) {
            if (p.first == s) mCardDetailsList.add(p)
        }
    }

    fun print() {
        for (s in mCardDetailsList) {
            println(s)
        }
    }
}