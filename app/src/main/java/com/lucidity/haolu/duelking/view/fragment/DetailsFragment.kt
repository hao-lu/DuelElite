package com.lucidity.haolu.duelking.view.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.lucidity.haolu.duelking.R
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class DetailsFragment : Fragment() {

    private val TAG = "DetailsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_details, container, false)
        val cardName = arguments!!.getString("cardName")
        rootView.findViewById<ProgressBar>(R.id.progressbar_details).visibility = View.VISIBLE
        ParseDetailsTask(context!!).execute(cardName)
        return rootView
    }

    /**
     * AsyncTask parses the details from the CARD_DETAIL_TYPES and adds them into a table
     *
     */
    private class ParseDetailsTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseDetailsTask"
        private val BASE_URL = "https://yugioh.wikia.com/wiki/"
        private val CARD_DETAIL_TYPES = arrayOf(
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
                "Description",
                "ImageUrl")

        private var mCardDetailsList: MutableList<Pair<String, String>> = mutableListOf()
        private val mActivity = context as AppCompatActivity

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val encoder = URLEncoder.encode(cardName!!, "UTF-8")
            val cardNamePath = encoder.replace("+", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {

                Log.d(TAG, "QUERY : " + cardNamePath.replace(" ", "_"))

                val document = Jsoup.connect(cardUrl).get()
                val cardTable: Element = document.getElementsByClass("cardtable").first()

                // <a href = ... >
                val imageUrl = cardTable.select("tr")[1].
                        getElementsByClass("cardtable-cardimage")[0].
                        select("a[href]")[0].
                        attr("href").toString()

                mCardDetailsList.add(Pair("ImageUrl", imageUrl))
                Log.d(TAG, imageUrl)

                // Get the header
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
                        addData(Pair(header, formatted))
                    }
                    else
                        addData(Pair(header, value))
                }
            }
            catch (httpStatusException: HttpStatusException) {
                Log.d(TAG, "No webpage")
            }
            catch (unknownHostException: UnknownHostException) {
                Log.d(TAG, "No Internet")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (mActivity.findViewById<ProgressBar>(R.id.progressbar_details) != null) {
                val progressBar = mActivity.findViewById(R.id.progressbar_details) as ProgressBar
                progressBar.visibility = ProgressBar.GONE
                for (detail in mCardDetailsList) {
                    if (detail.first != "ImageUrl") {
                        val row = View.inflate(context, R.layout.table_row_detail, null) as TableRow
                        val cardHeader = row.findViewById(R.id.text_card_header) as TextView
                        val cardValue = row.findViewById(R.id.text_card_value) as TextView
                        cardHeader.text = detail.first
                        cardValue.text = detail.second
                        val cardInformation = mActivity.findViewById(R.id.table_card_details) as TableLayout
                        cardInformation.addView(row)
                    }
                }
            }
        }

        /**
         * Adds the data to our list by checking if the header matches an attribute in
         * CARD_DETAIL_TYPES.
         *
         * @param p Pair of data for the header and data, e.g., (Card Type, Monster)
         */
        private fun addData(p: Pair<String, String>) {
            for (s in CARD_DETAIL_TYPES) {
                if (p.first == s) mCardDetailsList.add(p)
            }
        }
    }

}