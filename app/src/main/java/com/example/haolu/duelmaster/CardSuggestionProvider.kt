package com.example.haolu.duelmaster

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import io.realm.Case
import io.realm.Realm
import android.content.ContentResolver

class CardSuggestionProvider : ContentProvider() {

    private val TAG = "CardSuggestionProvider"

    companion object {
        val AUTHORITY = "com.example.haolu.duelmaster.CardSuggestionProvider"
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/cards")
    }

    private val SEARCH_SUGGEST = 0
    private val SEARCH_WORDS = 1
    private val GET_NAME = 2

    // Need _id column
    // The column names have to match (suggest_text_1)

    // MIME types used for searching words or looking up a single definition
    val WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.example.android.duelmaster"
    val DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.example.android.duelmaster"

    private var mUriMatcher = buildUriMatcher()

    private fun buildUriMatcher(): UriMatcher{
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        // Get suggestions
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST)
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST)

        // Select suggestion
        matcher.addURI(AUTHORITY, "cards/#", GET_NAME)

        // Go button pressed
        // See searchable
        matcher.addURI(AUTHORITY, "cards", SEARCH_WORDS)

        return matcher
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
//        Log.d(TAG, "HERE: " + uri.toString())
        when (mUriMatcher.match(uri)) {
            SEARCH_SUGGEST -> if (selectionArgs == null)
                throw IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri)
            else return getSuggestions(selectionArgs[0])
            SEARCH_WORDS -> if (selectionArgs == null)
                throw IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri)
            else return getCardList(selectionArgs[0])
            GET_NAME -> return getName(uri!!)
            else -> throw IllegalArgumentException()
        }
    }

    private fun getSuggestions(query: String): Cursor {
        val lowerCaseQuery = query.toLowerCase()

        // SUGGEST_COLUMN_INTENT_DATA_ID appends the path to content://com.example.haolu.duelmaster.CardSuggestionProvider/cards/#
        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)

        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(Card::class.java)
        val results = realmQuery.beginsWith("name", lowerCaseQuery, Case.INSENSITIVE).findAll()
        var size = 10
        if (results.size < 10) size = results.size
        val subList = results.subList(0, size)
//
        val matrixCursor = MatrixCursor(columns)
        for (r in subList) {
            val rowData = arrayOf(r.id, r.name, r.id)
            matrixCursor.addRow(rowData)
        }

//        Log.d(TAG, "query()")
//        Log.d(TAG, matrixCursor.count.toString())
        mRealm.close()
        return matrixCursor
    }

    private fun getCardList(query: String): Cursor {
        val lowerCaseQuery = query.toLowerCase()

        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1)

        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(Card::class.java)
        val results = realmQuery.beginsWith("name", lowerCaseQuery, Case.INSENSITIVE).findAll()
        var size = 10
        if (results.size < 10) size = results.size
        val subList = results.subList(0, size)
//
        val matrixCursor = MatrixCursor(columns)
        for (r in subList) {
            val rowData = arrayOf(r.id, r.name)
            matrixCursor.addRow(rowData)
        }

        mRealm.close()
        return matrixCursor
    }

    private fun getName(uri: Uri): Cursor {
        val rowId = uri.lastPathSegment.toInt()
//        Log.d(TAG, "Row ID : $rowId")
        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1)
        val selection = "rowId = ?"
        val selectionArgs = arrayOf(rowId)

        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(Card::class.java)
        val result = realmQuery.equalTo("id", rowId).findFirst()


        val matrixCursor = MatrixCursor(columns)
        val rowData = arrayOf(result.id, result.name)
        matrixCursor.addRow(rowData)

        mRealm.close()

        return matrixCursor
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        throw UnsupportedOperationException()
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri?): String {
//        when (mUriMatcher.match(uri)) {
//            SEARCH_WORDS -> return WORDS_MIME_TYPE
//            GET_NAME -> return DEFINITION_MIME_TYPE
//            SEARCH_SUGGEST -> return SearchManager.SUGGEST_MIME_TYPE
//            else -> throw IllegalArgumentException("Unknown URL " + uri)
//        }
        throw UnsupportedOperationException()
    }
}