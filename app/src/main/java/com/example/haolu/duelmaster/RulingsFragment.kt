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
import kotlinx.android.synthetic.main.fragment_rulings.*
import org.jsoup.HttpStatusException

class RulingsFragment : Fragment() {

    var mRulingsList: MutableList<String> = mutableListOf()
    var mTcgRulings = arrayListOf<String>()
    var mOcgRulings = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_rulings, container, false)
        val cardName = arguments.getString("cardName")
        ParseRulingsTask().execute(cardName)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mOcgRulings.clear()
        mTcgRulings.clear()
    }

    private inner class ParseRulingsTask : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseRulingsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/Card_Rulings:"

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
                    if (c.`is`("h2") && c.text() == "Previously Official Rulings") {
                        currentRuling = null
                    }
                    if (currentRuling != null) currentRuling?.add(c.text().replace(Regex("\\[\\w+\\s*\\w*\\]"), ""))
                }

            }

            catch (httpStatusException: HttpStatusException) {
                activity.runOnUiThread { activity.no_rulings.visibility = TextView.VISIBLE }
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
            for (tip in mTcgRulings) {
                val row = View.inflate(context, R.layout.fragment_table_row_tips, null)
                val cardHeader = row.findViewById(R.id.card_tip) as TextView
                cardHeader.text = tip
                table_tcg_rulings.addView(row)
            }
            for (tip in mOcgRulings) {
                val row = View.inflate(context, R.layout.fragment_table_row_tips, null)
                val cardHeader = row.findViewById(R.id.card_tip) as TextView
                cardHeader.text = tip
                table_ocg_rulings.addView(row)
            }

        }
    }
}