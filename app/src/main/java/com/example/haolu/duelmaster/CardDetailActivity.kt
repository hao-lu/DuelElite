package com.example.haolu.duelmaster

import android.app.Activity
import android.content.Context
import android.support.v4.content.CursorLoader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_card_detail.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder

class CardDetailActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    private val TAG = "CardDetailActivity"

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var mUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Enable and show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Remove the big title
        collapse_toolbar.isTitleEnabled = false

        // Show image
        image_header.setOnClickListener {
            val fragment = ImageDialogFragment()
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.replace(android.R.id.content, fragment).addToBackStack(null).commit()

        }

        // Needed for CursorLoader to get data
        mUri = intent.data

        mViewPager = container
        // Prevents the Fragments from being redrawn
        mViewPager.offscreenPageLimit = 3
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Invokes onCreateLoader()
        supportLoaderManager.initLoader(0, null, this)

    }

    // Setups ViewPager with different fragments and each fragment passes the card name
    private fun setupViewPagerandTabLayout(cardName: String) {
        val bundle = Bundle()
        bundle.putString("cardName", cardName)
        val detailsFragment = DetailsFragment()
        detailsFragment.arguments = bundle

        val rulingsFragment = RulingsFragment()
        rulingsFragment.arguments = bundle

        val tipsFragment = TipsFragment()
        tipsFragment.arguments = bundle

        mSectionsPagerAdapter.addFragment(detailsFragment, "DETAILS")
        mSectionsPagerAdapter.addFragment(rulingsFragment, "RULINGS")
        mSectionsPagerAdapter.addFragment(tipsFragment, "TIPS")
        mViewPager.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(mViewPager)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_card_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    // Loads the cursor with the data from the intent
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, mUri, null, null, null, null)
    }

    // After the cursor has been loaded with the name, the actionBar title can be set and the
    // fragments can get the name and begin to work
    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data!!.moveToFirst()) {
            val cardName = data.getString(data.getColumnIndex(data.getColumnName(1)))
            Log.d(TAG, data.getString(data.getColumnIndex(data.getColumnName(1))))
            supportActionBar?.title = data.getString(data.getColumnIndex(data.getColumnName(1)))
            setupViewPagerandTabLayout(cardName)
        }
    }

    class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragmentList = mutableListOf<Fragment>()
        private val mFragmentTitle = mutableListOf<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitle[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitle.add(title)
        }
    }

    private class LoadImageHeaderTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "ParseDetailsTask"
        private val BASE_URL = "http://yugioh.wikia.com/wiki/"
        private var imageUrl = ""

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val encoder = URLEncoder.encode(cardName!!, "UTF-8")
            val cardNamePath = encoder.replace("+", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {

                Log.d(TAG, "QUERY : " + cardNamePath?.replace(" ", "_"))

                val document = Jsoup.connect(cardUrl).get()
                // <table class = cardtable>

//                val cardTable: Element = document.select("table").first()
                val cardTable: Element = document.getElementsByClass("cardtable").first()

                // <a href = ... >
                imageUrl = cardTable.select("tr")[1].
                        getElementsByClass("cardtable-cardimage")[0].
                        select("a[href]")[0].
                        attr("href").toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            // no internet connection error
            // no webpage error
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val activity = context as Activity
            val image = activity.findViewById(R.id.image_header) as ImageView
            Picasso.with(context).load(imageUrl).into(image)

        }
    }

    fun dismissImageFragment(view: View) {
        supportFragmentManager.popBackStack()
    }
}
