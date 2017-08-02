package com.example.haolu.duelmaster

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class DetailsFragment : Fragment() {

    private val TAG = "DetailsFragment"

    lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_details, container, false)
        progressBar = rootView.findViewById(R.id.progresbar_details) as ProgressBar

        val cardName = arguments.getString("cardName")
        ParseDetailsTask(context).execute(cardName)

        return rootView
    }

    private class ParseDetailsTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseDetailsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/"
        private val CARD_DETAIL_TYPES = arrayOf(
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


        private var mCardDetailsList: MutableList<Pair<String, String>> = mutableListOf()

        override fun onPreExecute() {
            super.onPreExecute()
//            activity.runOnUiThread { progressBar.visibility = ProgressBar.VISIBLE }

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

                // Get the header
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
            super.onPostExecute(result)
//            progressBar.visibility = ProgressBar.GONE
            for (detail in mCardDetailsList) {
                if (detail.first != "ImageUrl") {
                    val row = View.inflate(context, R.layout.fragment_table_row_detail, null)
                    val cardHeader = row.findViewById(R.id.card_header) as TextView
                    val cardValue = row.findViewById(R.id.card_value) as TextView
                    cardHeader.text = detail.first
                    cardValue.text = detail.second
                    val card_information = (context as Activity).findViewById(R.id.table_card_details) as TableLayout
                    card_information.addView(row)
                }
            }

            val activity = context as Activity
            val image = activity.findViewById(R.id.image_header) as ImageView
            Picasso.with(context).load(mCardDetailsList[0].second).into(image)
        }

        private fun addData(p: Pair<String, String>) {
            for (s in CARD_DETAIL_TYPES) {
                if (p.first == s) mCardDetailsList.add(p)
            }
        }
    }

}