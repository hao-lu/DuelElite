package com.example.haolu.duelmaster

import android.app.LoaderManager
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
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search_card.*

class SearchCardActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    val TAG = "SearchCardActivity"
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

        mCursorAdapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1), IntArray(android.R.id.text1), 0)

//        var listView = findViewById(R.id.list_view) as ListView
//        mListView = findViewById(R.id.list_view) as ListView
//        mListView.adapter = mCursorAdapter

        handleIntent(intent)

        // Open the keyboard for the user
//        search_view_card.onActionViewExpanded()
//        search_view_card.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//            }
//        })


//        val searchManager = getSystemService(Context.SEARCH_SERVICE)
//        val componentName = ComponentName(applicationContext, CardDetailActivity::class.java)


        // Close the search activity
//        back_button.setOnClickListener { finish() }


    }

    // launchMode: singleTop - system routes the intent using onNewIntent()
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val searchIntent = intent
        Log.d("SearchCardActivity", Intent.ACTION_SEARCH.equals(searchIntent?.action).toString())
        Log.d("SearchCardActivity", searchIntent?.getStringExtra(SearchManager.QUERY).toString())
        Log.d("SearchCardActivity", searchIntent?.data.toString())
        handleIntent(intent!!)
    }

    private fun handleIntent(i: Intent) {
        if (Intent.ACTION_VIEW.equals(i.action)) {
            val detailIntent = Intent(this, CardDetailActivity::class.java)
            detailIntent.data = intent.data
            val uri = detailIntent.data.lastPathSegment
            startActivity(detailIntent)
        }
        else if (Intent.ACTION_SEARCH.equals(i.action)) {
            val query = i.getStringExtra(SearchManager.QUERY)
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show()
            println("Received query $query")
//            displayResults(i.data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_cards, menu)
        val searchItem = menu?.findItem(R.id.action_search) as MenuItem
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.onActionViewExpanded()
        // Can use getComponentName() because searchableActivity is the current activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

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

//        autoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
//            if (v.back)
//            if (v.id == R.id.search_src_text && !hasFocus) {
////                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                imm.hideSoftInputFromWindow(v.windowToken, 0)
//                autoCompleteTextView.requestFocus()
//            }
//        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun displayResults(uri: Uri) {
        val query = uri.lastPathSegment.toLowerCase()
        val cursorLoader = CursorLoader(this, CardSuggestionProvider.CONTENT_URI, null, null, arrayOf(query), null)
        // uri project selection selectionargs sort order

        val cursor = cursorLoader.loadInBackground()

        if (cursor == null) {
            // No search results
            Toast.makeText(this, "NO SEARCH FOR $query", Toast.LENGTH_SHORT).show()
        }
        else {
            val count = cursor.count.toString()
            Toast.makeText(this, count, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = CardSuggestionProvider.CONTENT_URI
//        val query = args?.getString("query")
//        Log.d(TAG, "query: $query")
        return CursorLoader(this, uri, null, null, arrayOf(args?.getString("query")), null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mCursorAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}