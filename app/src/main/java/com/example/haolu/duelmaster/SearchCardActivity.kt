package com.example.haolu.duelmaster

import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.app.SearchManager
import android.content.*
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView;
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_search_card.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager


import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.widget.SimpleCursorAdapter
import android.widget.ListView

class SearchCardActivity : AppCompatActivity(), LoaderCallbacks<Cursor>{

    private val TAG = "SearchCardActivity"

    lateinit var mListView: ListView
    lateinit var mCursorAdapter: SimpleCursorAdapter

    private class SearchQuery : AsyncTask<String, Void, Void>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }

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
            // This appends the rowId to the path content://com.example.haolu.duelmaster.CardSuggestionProvider/cards/#
            val data = Uri.withAppendedPath(CardSuggestionProvider.CONTENT_URI, id.toString())
            cardDetailIntent.setData(data)
            startActivity(cardDetailIntent)
        }

        mCursorAdapter = SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
                kotlin.IntArray(android.R.id.text1), 0)

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
            // Go button clciked
            println("Received query $query")
//            displayResults(query)
        }
    }

    private fun displayResults(query: String) {
        val data = Bundle()
        data.putString("query", query)

        // Invoke onCreateLoader() in non-UI thread
        supportLoaderManager.initLoader(1, data, this)
    }

    override fun onRestart() {
        val searchView = findViewById(R.id.action_search)
        val autoCompleteTextView = searchView.findViewById(R.id.search_src_text) as AutoCompleteTextView

        Log.d(TAG, "ON RESTART")
        autoCompleteTextView.showDropDown()
        super.onRestart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_cards, menu)
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.onActionViewExpanded()
        // Can use getComponentName() because searchableActivity is the current activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)

//        val autoCompleteTextView = searchView.findViewById(R.id.search_src_text) as AutoCompleteTextView
//        val dropDownAnchor = searchView.findViewById(autoCompleteTextView.dropDownAnchor)
//        // Need to add a listener to AutoCompleteTextView, AutoCompleteTextView's offset are calculated in SearchView each time bound is changed
//        if (dropDownAnchor != null) {
//            dropDownAnchor.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                var point = IntArray(2)
//                dropDownAnchor.getLocationOnScreen(point)
//                val dropDownPadding = point[0] + autoCompleteTextView.dropDownHorizontalOffset
//
//                val screenSize = Rect()
//                windowManager.defaultDisplay.getRectSize(screenSize)
//                val screenWidth = screenSize.width()
//j
//                autoCompleteTextView.dropDownWidth = screenWidth
//            }
//        }

//        autoCompleteTextView.imeOptions = EditorInfo.IME_ACTION_DONE

//        autoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
//            if (v.id == R.id.search_src_text && !hasFocus) {
////                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                imm.hideSoftInputFromWindow(v.windowToken, 0)
//                autoCompleteTextView.requestFocus()
//                autoCompleteTextView.showDropDown()
//            }
//        }
//
//        autoCompleteTextView.setOnEditorActionListener { v, actionId, event -> {}}

//        autoCompleteTextView.setOnEditorActionListener(object: TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_DONE)
//                    return true
//                else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(v?.windowToken, 0)
//                    autoCompleteTextView.clearFocus()
//                    searchView.clearFocus()
//                    return true
//                }
//
//                return false
//            }
//        })

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = CardSuggestionProvider.CONTENT_URI
		return CursorLoader(getBaseContext(), uri, null, null , arrayOf(args?.getString("query")), null)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mCursorAdapter.swapCursor(data)
    }
}