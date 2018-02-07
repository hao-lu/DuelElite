package com.lucidity.haolu.duelking.view.activity

import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.app.SearchManager
import android.content.*
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import kotlinx.android.synthetic.main.activity_search_card.*
import android.widget.*
import android.content.Intent

import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.widget.SimpleCursorAdapter
import android.widget.ListView
import com.lucidity.haolu.duelking.CardSuggestionProvider
import com.lucidity.haolu.duelking.R

class SearchableCardActivity : AppCompatActivity(), LoaderCallbacks<Cursor>{

    private val TAG = "SearchableCardActivity"

    lateinit var mListView: ListView // ListView that displays when user clicks go
    lateinit var mCursorAdapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_card)
        setSupportActionBar(toolbar)
        // Remove title from toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Enable and show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mListView = findViewById(R.id.list_view) as ListView
        mListView.setOnItemClickListener { parent, view, position, id ->
            val cardDetailIntent = Intent(this, CardDetailActivity::class.java)
            // This appends the rowId to the path content://com.lucidity.haolu.duelking.CardSuggestionProvider/cards/#
            val data = Uri.withAppendedPath(CardSuggestionProvider.CONTENT_URI, id.toString())
            cardDetailIntent.setData(data)
            startActivity(cardDetailIntent)
        }

        // Set the cursor from loader to a simple list (mListView)
        mCursorAdapter = SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
                intArrayOf(android.R.id.text1), 0)

        mListView.adapter = mCursorAdapter

        handleIntent(intent)
    }

    // launchMode: singleTop - system routes the intent using onNewIntent()
    override fun onNewIntent(intent: Intent?) {
        handleIntent(intent!!)
    }

    private fun handleIntent(i: Intent) {
        if (i.action == Intent.ACTION_VIEW) {
            // Suggestion clicked
            val detailIntent = Intent(this, CardDetailActivity::class.java)
            detailIntent.data = i.data
            startActivity(detailIntent)
        }
        else if (i.action == Intent.ACTION_SEARCH) {
            val query = i.getStringExtra(SearchManager.QUERY)
            // Go button clicked
            displayResults(query)
        }
    }

    private fun displayResults(query: String) {
        val data = Bundle()
        data.putString("query", query)
        /* Invoke onCreateLoader() in non-UI thread
        supportLoaderManager.initLoader(1, data, this)
        Use restart to change the data of the query since we're using the same Activity */
        supportLoaderManager.restartLoader(1, data, this)
    }

    // Show the dropdown when the user returns from CardDetailActivity
    override fun onRestart() {
        super.onRestart()
        val searchView = findViewById(R.id.action_search)
        val autoCompleteTextView = searchView.findViewById(R.id.search_src_text) as AutoCompleteTextView
        autoCompleteTextView.showDropDown()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_cards, menu)
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.onActionViewExpanded()
        // Can use getComponentName() because searchableActivity is the current activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Int.MAX_VALUE

        val autoCompleteTextView = searchView.findViewById(R.id.search_src_text) as AutoCompleteTextView
        val dropDownAnchor = searchView.findViewById(autoCompleteTextView.dropDownAnchor)
        // Need to add a listener to AutoCompleteTextView, AutoCompleteTextView's offset are calculated in SearchView each time bound is changed
        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                var point = IntArray(2)
                dropDownAnchor.getLocationOnScreen(point)
                val dropDownPadding = point[0] + autoCompleteTextView.dropDownHorizontalOffset

                val screenSize = Rect()
                windowManager.defaultDisplay.getRectSize(screenSize)
                val screenWidth = screenSize.width()
                autoCompleteTextView.dropDownWidth = screenWidth
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = CardSuggestionProvider.CONTENT_URI
		return CursorLoader(baseContext, uri, null, null , arrayOf(args?.getString("query")), null)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        // supportLoaderManager.restartLoader()
        // Invokes onLoadFinished again
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mCursorAdapter.swapCursor(data)
    }
}