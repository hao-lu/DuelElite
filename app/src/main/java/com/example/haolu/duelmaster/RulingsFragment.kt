package com.example.haolu.duelmaster

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Element

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

        private var mRulingsList = arrayListOf<RulingsRecyclerViewAdapter.HeaderOrItem>()

        private val activity = context as Activity

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val cardNamePath = cardName?.replace(" ", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {

                val document = Jsoup.connect(cardUrl).get()
                val article = document.select("article").first()
                val div = article.getElementById("mw-content-text")
                val children = div.children()

                var addToList = false
                for (c in children) {
                    // Top header
                    if (c.`is`("h2") && c.text() == "TCG Rulings" ||
                            c.text() == "OCG Rulings" ||
                            c.text() == "Previously Official Rulings") {
                        addToList = true
                    }
                    else if (c.`is`("h2")) addToList = false
//                    else if (c.`is`("h2") && c.text() != "TCG Rulings" &&
//                            c.text() != "OCG Rulings" &&
//                            c.text() != "Previously Official Rulings") {
//                        addToList = false
//                    }

                    if (addToList) addItem(c)

                }

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


        // TODO: BUG if internet is slow and the user switches viewpager quickly, null on recyclerview
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if ((context as Activity).findViewById(R.id.progressbar_rulings) != null) {
            val progressBar = (context as Activity).findViewById(R.id.progressbar_rulings) as ProgressBar
            progressBar.visibility = ProgressBar.GONE
            if (mRulingsList.size != 0) {
                val simpleAdapter = RulingsRecyclerViewAdapter(mRulingsList)
                val layoutManger = LinearLayoutManager(activity)
                val tipList = activity.findViewById(R.id.tcg_ruling_rv) as RecyclerView
                val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                tipList.addItemDecoration(itemDecoration)
                tipList.layoutManager = layoutManger
                tipList.adapter = simpleAdapter
            }
            else {
                val noRulingText = (context as Activity).findViewById(R.id.text_no_rulings) as TextView
                noRulingText.visibility = TextView.VISIBLE
            }
                }
        }

        private fun addItem(c: Element) {
            // Removes the superscripts (foot notes)
            val text = c.text().replace(Regex("((References: )*\\[.*?\\])"), "")
//            val text = c.text().replace(Regex("\\[.*?\\]"), "")
            if (c.`is`("h2"))
                mRulingsList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.H2, text))
            // Subheader
            else if (c.`is`("h3"))
                mRulingsList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.H3, text))
            // Information table
            else if (c.`is`("table"))
                mRulingsList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.TABLE, text))
            // Div has children (red / green box)
            else if (c.`is`("div")) {
                val grandChildren = c.children()
                for (g in grandChildren) {
                    addItem(g)
                }
            }
            // Item
            else if (c.`is`("ul"))
                mRulingsList.add(RulingsRecyclerViewAdapter.
                        HeaderOrItem(RulingsRecyclerViewAdapter.
                                HeaderOrItem.Types.UL, text))
        }
    }


}