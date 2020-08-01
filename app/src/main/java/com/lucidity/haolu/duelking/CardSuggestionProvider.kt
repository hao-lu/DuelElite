package com.lucidity.haolu.duelking

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.lucidity.haolu.duelking.model.OldCard
import io.realm.Case
import io.realm.Realm

class CardSuggestionProvider : ContentProvider() {

    private val TAG = "CardSuggestionProvider"

    companion object {
        val AUTHORITY = "com.lucidity.haolu.duelking.CardSuggestionProvider"
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/cards")
    }

    // Different ID for each case
    private val SEARCH_SUGGEST = 0
    private val SEARCH_WORDS = 1
    private val GET_NAME = 2

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

    // Get the cursor with the suggestions
    private fun getSuggestions(query: String): Cursor {
        /* SUGGEST_COLUMN_INTENT_DATA_ID appends the path to content://com.example.haolu.duelmaster.CardSuggestionProvider/cards/#
        Need _id column, the column names have to match (suggest_text_1)
        Need the suggest_column_intent_data_id to launch intent on click suggestion */
        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)
        // RealmQuery.like(): ? matches a single unicode char e.g. space
        val lowerCaseQuery = query.toLowerCase().replace(" ", "?")
        // blue eyes = blue?eyes*
        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(OldCard::class.java)
        val results = realmQuery.like("name", "$lowerCaseQuery*", Case.INSENSITIVE).findAll()
//        val results = realmQuery.beginsWith("name", lowerCaseQuery, Case.INSENSITIVE).findAll()
        var suggestionSize = 10
        if (results.size < suggestionSize) suggestionSize = results.size
        val subList = results.subList(0, suggestionSize)

        val matrixCursor = MatrixCursor(columns)
        for (r in subList) {
            val rowData = arrayOf(r.id, r.name, r.id)
            matrixCursor.addRow(rowData)
        }

        mRealm.close()
        return matrixCursor
    }

    // Get the cursor with suggestions (similar to getSuggestion)
    private fun getCardList(query: String): Cursor {
        val lowerCaseQuery = query.toLowerCase()

        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1)

        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(OldCard::class.java)
//        val results = realmQuery.beginsWith("name", lowerCaseQuery, Case.INSENSITIVE).findAll()
        val results = realmQuery.like("name", "*$lowerCaseQuery*", Case.INSENSITIVE).findAll()
        val matrixCursor = MatrixCursor(columns)
        for (r in results) {
            val rowData = arrayOf(r.id, r.name)
            matrixCursor.addRow(rowData)
        }
        mRealm.close()
        return matrixCursor
    }

    // Get the name of the the suggestion clicked on
    private fun getName(uri: Uri): Cursor {
        val rowId = uri.lastPathSegment.toInt()
        // SUGGEST_COLUMN_TEXT_1 = name
        val columns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1)

        val mRealm = Realm.getDefaultInstance()
        val realmQuery = mRealm.where(OldCard::class.java)
        val result = realmQuery.equalTo("id", rowId).findFirst()

        val matrixCursor = MatrixCursor(columns)
        val rowData = arrayOf(result!!.id, result.name)
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
        throw UnsupportedOperationException()
    }
}