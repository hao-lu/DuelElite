package com.lucidity.haolu

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.lucidity.haolu.R
import org.jsoup.Jsoup
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException


/**
 * Parses the ruling information
 */

class RulingsFragment : Fragment() {

    private val TAG = "RulingsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_rulings_new, container, false)
        val cardName = arguments!!.getString("cardName")
        rootView.findViewById<ProgressBar>(R.id.progressbar_rulings).visibility = View.VISIBLE
        ParseRulingsTask(context!!).execute(cardName)
        return rootView
    }

    override fun onPause() {
        super.onPause()

    }

    private class ParseRulingsTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseRulingsTask"
        private val BASE_URL = "https://yugioh.wikia.com/wiki/Card_Rulings:"

        // ArrayList of ArrayList to section different headers, i.e., TCG Rulings vs OCG Rulings
        private var mRulingsList = ArrayList<ArrayList<RulingsRecyclerViewAdapter.HeaderOrItem>>()
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
                val document = Jsoup.connect(cardUrl).get()
                val article = document.select("article").first()
                val div = article.getElementById("mw-content-text")
                val children = div.children()
                var addToList = false
                for (c in children) {
                    // Add all header sections excluding References and Notes
                    if (c.`is`("h2") && c.text() != "References" &&
                        c.text() != "Notes") {
                        addToList = true
                        // New header
                        mRulingsList.add(arrayListOf())
                        Log.d(TAG, c.text())
                    } else if (c.`is`("h2")) addToList = false
                    if (addToList) addItem(c)
                }

                Log.d(TAG, mRulingsList.size.toString())

            } catch (httpStatusException: HttpStatusException) {
                Log.d(TAG, "httpStatusException")
            } catch (unknownHostException: UnknownHostException) {
                Log.d(TAG, "No Internet")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // no internet connection error
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            // Fixes bug when internet is slow and the user switches viewpager quicker, checks for null
            if (mActivity.findViewById<ProgressBar>(R.id.progressbar_rulings) != null) {
                val progressBar = mActivity.findViewById(R.id.progressbar_rulings) as ProgressBar
                progressBar.visibility = ProgressBar.GONE
                if (mRulingsList.size != 0) {
                    val simpleAdapter = RulingsRecyclerViewAdapter(mRulingsList)
                    val layoutManger = LinearLayoutManager(mActivity)
                    val tipList = mActivity.findViewById(R.id.tcg_ruling_rv) as RecyclerView
                    tipList.layoutManager = layoutManger
                    tipList.adapter = simpleAdapter
                } else {

                    val noRulingLayout = mActivity.findViewById(R.id.empty_no_rulings) as LinearLayout
                    noRulingLayout.visibility = View.VISIBLE
                }
            }
        }

        /**
         * Add a new RulingsRecylcerViewAdapter.HeaderOrItem item into the last ArrayList<ArrayList<..>>
         *
         */
        private fun addItem(c: Element) {
            val rulingList = mRulingsList[mRulingsList.size - 1]
            // Removes the superscripts (foot notes)
            val text = c.text().replace(Regex("((References: )*\\[.*?\\])"), "")
            if (c.`is`("h2"))
                rulingList.add(RulingsRecyclerViewAdapter.HeaderOrItem(RulingsRecyclerViewAdapter.HeaderOrItem.Types.H2, text))
            // Subheader
            else if (c.`is`("h3"))
                rulingList.add(RulingsRecyclerViewAdapter.HeaderOrItem(RulingsRecyclerViewAdapter.HeaderOrItem.Types.H3, text))
            // Div has children (red / green box)
            else if (c.`is`("div")) {
                val grandChildren = c.children()
                for (g in grandChildren) {
                    addItem(g)
                }
            }
            // Item
            else if (c.`is`("ul"))
                rulingList.add(RulingsRecyclerViewAdapter.HeaderOrItem(RulingsRecyclerViewAdapter.HeaderOrItem.Types.UL, text))
        }
    }

}