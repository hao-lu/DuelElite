package com.example.haolu.duelmaster

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlinx.android.synthetic.main.fragment_tips.*

class TipsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private class ParseTipsTask : AsyncTask<String, Void, Void>() {
        val TAG = "ParseDetailsTask"
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {
            try {
                val document = Jsoup.connect("http://yugioh.wikia.com/wiki/Stardust_Dragon").get()
                val cardTable: Element = document.select("table").first()

                // Get 2nd table row where the card image link is located
                val imageLink = cardTable.select("tr")[1].
                        getElementsByClass("cardtable-cardimage")[0].
                        select("a[href]")[0].
                        attr("href").toString()

                val test = cardTable.getElementsByClass("cardtablerow")[0].text()

                val cardTableRows = cardTable.getElementsByClass("cardtablerow")

                val description = "f"
                val types = "f"
                val status = "f"
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }

    }
}