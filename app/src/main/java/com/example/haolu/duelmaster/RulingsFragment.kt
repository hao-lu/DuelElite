package com.example.haolu.duelmaster

import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haolu.duelmaster.databinding.FragmentDetailsBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlinx.android.synthetic.main.fragment_rulings.*


class RulingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = inflater!!.inflate(R.layout.fragment_details, container, false)
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rulings, container, false)
        val rootView = binding.root

        val cardName = arguments.getString("cardName")

//
//        binding.setVariable(BR.CardDetails, mCardDetails)

        ParseRulingsTask().execute(cardName)


        return rootView
    }

    private inner class ParseRulingsTask : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseDetailsTask"
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
                // <table class = cardtable>
                val cardTable: Element = document.select("table").first()

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
        }

    }
}