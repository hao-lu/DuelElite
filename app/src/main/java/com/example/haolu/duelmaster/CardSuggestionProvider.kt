package com.example.haolu.duelmaster

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import io.realm.Case
import io.realm.Realm

class CardSuggestionProvider : ContentProvider() {

    val TAG = "CardSuggestionProvider"

    companion object {
        val AUTHORITY = "com.example.haolu.duelmaster.CardSuggestionProvider"
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/cards")
    }

    // Need _id column
    // The column names have to match (suggest_text_1)
    val mColumns = arrayOf("_ID", SearchManager.SUGGEST_COLUMN_TEXT_1)

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val queryText = uri?.lastPathSegment?.toLowerCase()
        Log.d(TAG, queryText)

        val mRealm = Realm.getDefaultInstance()
        val query = mRealm.where(Card::class.java)
        val results = query.beginsWith("name", queryText, Case.INSENSITIVE).findAll()
        var size = 10
        if (results.size < 10) size = results.size
        val subList = results.subList(0, size)

//        for (s in subList) {
//            Log.d(TAG, s.name)
//        }


//        Log.d(TAG, results.name)

        val matrixCursor = MatrixCursor(mColumns)
        var id = 0
        for (r in subList) {
            val rowData = arrayOf(id, r.name)
            matrixCursor.addRow(rowData)
            id++
        }

//        val rowData = arrayOf("0", results.name)
//        matrixCursor.addRow(rowData)

        Log.d(TAG, "query()")
        Log.d(TAG, matrixCursor.count.toString())
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