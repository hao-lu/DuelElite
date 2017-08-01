package com.example.haolu.duelmaster

import android.support.v4.content.CursorLoader;
import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_card_detail.*
import kotlinx.android.synthetic.main.activity_search_card.*
import android.support.design.widget.CollapsingToolbarLayout
import android.widget.Toast
import com.example.haolu.duelmaster.databinding.ActivityCardDetailBinding
import com.example.haolu.duelmaster.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso


class CardDetailActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    /**
     * The [ViewPager] that will host the section contents.
     */
    private lateinit var mViewPager: ViewPager

    private val TAG = "CardDetailActivity"

    private lateinit var mUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)
//        val binding: ActivityCardDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_detail)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Remove title from toolbar
//        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Enable and show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        collapse_toolbar.isTitleEnabled = false
//        card_detail_appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset -> this }



        header.setOnClickListener { Toast.makeText(this, "Show image", Toast.LENGTH_SHORT).show()}


        mUri = intent.data

        mViewPager = container
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        supportLoaderManager.initLoader(0, null, this)
    }

    private fun setupViewPagerandTabLayout(cardName: String) {
        val bundle = Bundle()
        bundle.putString("cardName", cardName)
        val detailsFragment = DetailsFragment()
        detailsFragment.arguments = bundle

//        val rulings = DetailsFragment()
//        rulings.arguments = bundle

//        val tips = DetailsFragment()
//        tips.arguments = bundle

        mSectionsPagerAdapter.addFragment(detailsFragment, "DETAILS")
//        mSectionsPagerAdapter.addFragment(rulings, "RULINGS")
//        mSectionsPagerAdapter.addFragment(tips, "TIPS")
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, mUri, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data!!.moveToFirst()) {
            val cardName = data.getString(data.getColumnIndex(data.getColumnName(1)))
            Log.d(TAG, data.getString(data.getColumnIndex(data.getColumnName(1))))
            supportActionBar?.title = data.getString(data.getColumnIndex(data.getColumnName(1)))
            setupViewPagerandTabLayout(cardName)
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

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
}
