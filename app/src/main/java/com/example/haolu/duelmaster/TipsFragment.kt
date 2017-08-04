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
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.HttpStatusException

class TipsFragment : Fragment() {

    private val TAG = "TipsFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_tips, container, false)
        val cardName = arguments.getString("cardName")
        ParseTipsTask(context).execute(cardName)
        return rootView
    }

    private class ParseTipsTask(val context: Context): AsyncTask<String, Void, Void>() {
        private val TAG = "ParseTipsTask"

        private val BASE_URL = "http://yugioh.wikia.com/wiki/Card_Tips:"

        private val activity = context as Activity
        private var mTipsList: ArrayList<String> = arrayListOf()

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
                activity.runOnUiThread { activity.findViewById(R.id.no_tips).visibility = TextView.VISIBLE }
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

            // Lb the world chalice priestess
            Log.d(TAG, mTipsList.size.toString())
            val simpleAdapter = TipsRecyclerViewAdapter(mTipsList)
            val layoutManger = LinearLayoutManager(activity)
            val tipList = activity.findViewById(R.id.tips_recycler_view) as RecyclerView
            val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            tipList.addItemDecoration(itemDecoration)
            tipList.layoutManager = layoutManger
            tipList.adapter = simpleAdapter
        }

    }
}