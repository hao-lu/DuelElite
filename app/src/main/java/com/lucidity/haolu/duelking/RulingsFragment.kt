package com.lucidity.haolu.duelking

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Element
import java.net.URLEncoder

class RulingsFragment : Fragment() {

    private val TAG = "RulingsFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_rulings, container, false)
        val cardName = arguments.getString("cardName")
        rootView.findViewById(R.id.progressbar_rulings).visibility = View.VISIBLE
        ParseRulingsTask(context).execute(cardName)
        return rootView
    }


    override fun onPause() {
        super.onPause()

    }

    private class ParseRulingsTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseRulingsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/Card_Rulings:"

        private var mRulingsList = ArrayList<ArrayList<RulingsRecyclerViewAdapter.HeaderOrItem>>()

        private val activity = context as Activity

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val encoder = URLEncoder.encode(cardName!!, "UTF-8")
            val cardNamePath = encoder.replace("+", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {

                val document = Jsoup.connect(cardUrl).get()
                val article = document.select("article").first()
                val div = article.getElementById("mw-content-text")
                val children = div.children()
                var addToList = false
                for (c in children) {
                    // Top header
//                    if (c.`is`("h2") && c.text() == "TCG Rulings" ||
//                            c.text() == "OCG Rulings" ||
//                            c.text() == "Previously Official Rulings") {
                    if (c.`is`("h2") && c.text() != "References" &&
                            c.text() != "Notes") {
                        addToList = true
                        mRulingsList.add(arrayListOf())
                        Log.d(TAG, c.text())
                    }
                    else if (c.`is`("h2")) addToList = false
//                    else if (c.`is`("h2") && c.text() != "TCG Rulings" &&
//                            c.text() != "OCG Rulings" &&
//                            c.text() != "Previously Official Rulings") {
//                        addToList = false
//                    }

                    if (addToList) addItem(c)

                }

                Log.d(TAG, mRulingsList.size.toString())

            }

            catch (httpStatusException: HttpStatusException) {
                Log.d(TAG, "httpStatusException")
//                activity.runOnUiThread { activity.findViewById(R.id.progressbar_rulings).visibility = TextView.GONE }
//                activity.runOnUiThread { activity.findViewById(R.id.text_no_rulings).visibility = TextView.VISIBLE }
            }

            catch (e: Exception) {
                e.printStackTrace()
            }

            // no internet connection error
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            // Fixes bug when internet is slow and the user switches viewpager quicker, checks for null
            if ((context as Activity).findViewById(R.id.progressbar_rulings) != null) {
            val progressBar = (context as Activity).findViewById(R.id.progressbar_rulings) as ProgressBar
            progressBar.visibility = ProgressBar.GONE
            if (mRulingsList.size != 0) {
                val simpleAdapter = RulingsRecyclerViewAdapter(mRulingsList)
                val layoutManger = LinearLayoutManager(activity)
                val tipList = activity.findViewById(R.id.tcg_ruling_rv) as RecyclerView
//                val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
//                tipList.addItemDecoration(itemDecoration)
                tipList.layoutManager = layoutManger
                tipList.adapter = simpleAdapter
            }
            else {
//                val noRulingText = (context as Activity).findViewById(R.id.text_no_rulings) as TextView
//                noRulingText.visibility = TextView.VISIBLE
                val noRulingText = (context as Activity).findViewById(R.id.empty_no_rulings) as LinearLayout
                noRulingText.visibility = View.VISIBLE
            }
                }
        }

        private fun addItem(c: Element) {
            val rulingList = mRulingsList[mRulingsList.size - 1]
            // Removes the superscripts (foot notes)
            val text = c.text().replace(Regex("((References: )*\\[.*?\\])"), "")
//            val text = c.text().replace(Regex("\\[.*?\\]"), "")
            if (c.`is`("h2"))
                rulingList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.H2, text))
            // Subheader
            else if (c.`is`("h3"))
                rulingList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.H3, text))
//            // Information table
//            else if (c.`is`("table"))
//                rulingList.add(RulingsRecyclerViewAdapter.
//                        HeaderOrItem(RulingsRecyclerViewAdapter.
//                                HeaderOrItem.Types.TABLE, text))
            // Div has children (red / green box)
            else if (c.`is`("div")) {
                val grandChildren = c.children()
                for (g in grandChildren) {
                    addItem(g)
                }
            }
            // Item
            else if (c.`is`("ul"))
                rulingList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.UL, text))
        }
    }


}