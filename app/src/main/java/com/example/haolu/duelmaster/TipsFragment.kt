package com.example.haolu.duelmaster

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_rulings.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlinx.android.synthetic.main.fragment_tips.*
import org.jsoup.HttpStatusException

class TipsFragment : Fragment() {

    var mTipsList: ArrayList<String> = arrayListOf()

    private val TAG = "TipsFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_tips, container, false)
        val cardName = arguments.getString("cardName")
        ParseTipsTask().execute(cardName)
        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mTipsList.clear()
    }

    private inner class ParseTipsTask : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseTipsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/Card_Tips:"

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
                val context = article.getElementById("mw-content-text")
                val children = context.children()
                for (c in children) {
//                    if (c.`is`("ul")) Log.d(TAG, c.text())
                    if (c.text() == "Traditional Format" || c.text() == "List")
                        break
//                    Log.d(TAG, c.text())
                    if (c.`is`("ul")) mTipsList.add(c.text())
                }

            }

            catch (httpStatusException: HttpStatusException) {
                activity.runOnUiThread { activity.no_tips.visibility = TextView.VISIBLE }
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
            Log.d(TAG, mTipsList.size.toString())
            for (tip in mTipsList) {

                val row = View.inflate(context, R.layout.fragment_table_row_tips, null)
                val cardHeader = row.findViewById(R.id.card_tip) as TextView
                cardHeader.text = tip
                card_tips.addView(row)
            }

        }

    }
}