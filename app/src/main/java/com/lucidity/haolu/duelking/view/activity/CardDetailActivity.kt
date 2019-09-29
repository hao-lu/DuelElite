package com.lucidity.haolu.duelking.view.activity

import android.content.Context
import android.content.Intent
import android.support.v4.content.CursorLoader
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.*
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.view.fragment.DetailsFragment
import com.lucidity.haolu.duelking.view.fragment.ImageDialogFragment
import com.lucidity.haolu.duelking.view.fragment.RulingsFragment
import com.lucidity.haolu.duelking.view.fragment.TipsFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_card_detail.*
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class CardDetailActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    private val TAG = "CardDetailActivity"
    private val LOADER_ID = 20190928

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var mUri: Uri
    private var mCardName: String = "" // Used for Intent to web browser to open Wikia page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Enable and show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        collapse_toolbar.isTitleEnabled = false

        // Needed for CursorLoader to get data
        mUri = intent.data

        mViewPager = container
        // Prevents the Fragments from being redrawn
        mViewPager.offscreenPageLimit = 3
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Invokes onCreateLoader()
        supportLoaderManager.initLoader(LOADER_ID, null, this)

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
        menuInflater.inflate(R.menu.menu_card_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_open_wikia) {
            val encoder = URLEncoder.encode(mCardName, "UTF-8")
            val cardNamePath = encoder.replace("+", "_")
            val cardUrl = "https://yugioh.wikia.com/wiki/" + cardNamePath
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(cardUrl)
            startActivity(i)
        }

        else if (id == R.id.action_open_ygoPrices) {
            val encoder = URLEncoder.encode(mCardName, "UTF-8")
            val cardUrl = "https://yugiohprices.com/card_price?name=" + encoder
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(cardUrl)
            startActivity(i)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // supportLoaderManager.restartLoader()
    }

    // Loads the cursor with the data from the intent
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, mUri, null, null, null, null)
    }

    // After the cursor has been loaded with the name, the actionBar title can be set and the
    // fragments can get the name and begin to work
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data!!.moveToFirst()) {
            val cardName = data.getString(data.getColumnIndex(data.getColumnName(1)))
            Log.d(TAG, data.getString(data.getColumnIndex(data.getColumnName(1))))
            supportActionBar?.title = data.getString(data.getColumnIndex(data.getColumnName(1)))
            mCardName = cardName
            setupViewPagerandTabLayout(cardName)
            LoadImageHeaderTask(this).execute(cardName)
            // Remove loader from adding more pagers
            supportLoaderManager.destroyLoader(LOADER_ID)
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

    /**
     * Gets the mImageUrl for the card
     */
    private class LoadImageHeaderTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "LoadImageHeaderTask"
        private val BASE_URL = "https://yugioh.wikia.com/wiki/"
        private var mImageUrl = ""
        private lateinit var mTarget: Target
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
                Log.d(TAG, "QUERY : " + cardNamePath.replace(" ", "_"))
                val document = Jsoup.connect(cardUrl).get()
                // <table class = cardtable>
                val cardTable: Element = document.getElementsByClass("cardtable").first()
                // <a href = ... >
                mImageUrl = cardTable.select("tr")[1].
                        getElementsByClass("cardtable-cardimage")[0].
                        select("a[href]")[0].
                        attr("href").toString()
                Log.d(TAG, mImageUrl)
            } catch (httpStatusException: HttpStatusException) {
                Log.d(TAG, "No webpage")
            } catch (unknownHostException: UnknownHostException) {
                mActivity.runOnUiThread {  Toast.makeText(context, "No internet connection. Connect to the internet and try again.", Toast.LENGTH_LONG).show() }
                Log.d(TAG, "No Internet")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (mImageUrl != "") {
                // Load image header with card image
//                val mActivity = context as AppCompatActivity
                val imageHeader = mActivity.findViewById(R.id.image_header) as ImageView
                val collapseToolbar = mActivity.findViewById(R.id.collapse_toolbar) as CollapsingToolbarLayout
                val tabs = mActivity.findViewById(R.id.tabs) as TabLayout
                Picasso.with(context).load(mImageUrl).into(imageHeader)

                mTarget = object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(errorDrawable: Drawable?) {}

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                    imageHeader.setImageBitmap(bitmap)
                        val swatch = Palette.from(bitmap!!).setRegion(25, 25, 35, 35).generate()
                        val dominant = swatch.dominantSwatch
                        if (dominant == null) Log.d(TAG, "DOMINANT NULL")
                        if (dominant != null) {
                            collapseToolbar.setContentScrimColor(dominant.rgb)
                            tabs.setSelectedTabIndicatorColor(dominant.rgb)
                            mActivity.window.statusBarColor = dominant.rgb
                        }
//                        val color = swatch.lightVibrantSwatch
//                        if (color != null) {
//                            tabs.setSelectedTabIndicatorColor(color.rgb)
//                        }
                    }
                }
                Picasso.with(context).load(mImageUrl).into(mTarget)

                // Load fragment when image is clicked
                imageHeader.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("imageUrl", mImageUrl)

                    val fragment = ImageDialogFragment()
                    fragment.arguments = bundle
                    val ft = mActivity.supportFragmentManager.beginTransaction()
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    ft.replace(android.R.id.content, fragment).addToBackStack(null).commit()
                }
            }
        }
    }

    /**
     * Dismiss fragment when clicked on
     */
    fun dismissImageFragment(view: View) {
        supportFragmentManager.popBackStack()
    }
}
