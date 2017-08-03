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
import android.widget.TableLayout
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.HttpStatusException

class RulingsFragment : Fragment() {

    private val TAG = "RulingsFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_rulings, container, false)
        val cardName = arguments.getString("cardName")
        ParseRulingsTask(context).execute(cardName)
        return rootView
    }

    private class ParseRulingsTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseRulingsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/Card_Rulings:"

        private var mTcgRulings = arrayListOf<String>()
        private var mOcgRulings = arrayListOf<String>()

        private val activity = context as Activity

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
                val article = document.select("article").first()
                val div = article.getElementById("mw-content-text")

                val children = div.children()

                var currentRuling: ArrayList<String>? = null

                for (c in children) {
                    if (c.`is`("h2") && c.text() == "TCG Rulings") {
                        currentRuling = mTcgRulings

                    }
                    if (c.`is`("h2") && c.text() == "OCG Rulings") {
                        currentRuling = mOcgRulings
                    }
                    if (c.`is`("h2") && c.text() != "TCG Rulings" && c.text() != "OCG Rulings") {
                        currentRuling = null
                    }
                    if (currentRuling != null) currentRuling?.add(c.text().replace(Regex("\\[\\w+\\s*\\w*\\]"), ""))
                }

            }

            catch (httpStatusException: HttpStatusException) {
                activity.runOnUiThread { activity.findViewById(R.id.no_rulings).visibility = TextView.VISIBLE }
            }

            catch (e: Exception) {
                e.printStackTrace()
            }

            // no internet connection error
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            for (tip in mTcgRulings) {
                val row = View.inflate(context, R.layout.fragment_table_row_ruling, null)
                val cardHeader = row.findViewById(R.id.textview_ruling) as TextView
                cardHeader.text = tip
                (activity.findViewById(R.id.table_tcg_rulings) as TableLayout).addView(row)
            }
            for (tip in mOcgRulings) {
                val row = View.inflate(context, R.layout.fragment_table_row_ruling, null)
                val cardHeader = row.findViewById(R.id.textview_ruling) as TextView
                cardHeader.text = tip
                (activity.findViewById(R.id.table_ocg_rulings) as TableLayout).addView(row)
            }
            // No TCG rulings or OCG rulings
            if (mTcgRulings.size == 0 && mOcgRulings.size ==0) {
                (activity.findViewById(R.id.no_rulings) as TextView).text = "No current official rulings available"
                activity.findViewById(R.id.no_rulings).visibility = TextView.VISIBLE
            }

        }
    }
}