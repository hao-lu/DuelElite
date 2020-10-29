package com.lucidity.haolu.searchcards

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lucidity.haolu.searchcards.room.dao.CardDao
import com.lucidity.haolu.searchcards.room.dao.RecentSearchDao
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.room.entity.RecentSearch

@Database(entities = arrayOf(Card::class, RecentSearch::class), version = 1)
abstract class SearchCardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        var instance: SearchCardsDatabase? = null
        fun getInstance(context: Context): SearchCardsDatabase? {
            if (instance == null) {
                synchronized(SearchCardsDatabase::class.java) {
                    if (instance == null) {
                        instance = buildDataBaseBuild(context)
                    }
                }
            }
            return instance
        }

        private fun buildDataBaseBuild(context: Context): SearchCardsDatabase {
            return Room
                .databaseBuilder(context, SearchCardsDatabase::class.java, "searchCardsDb")
                .build()
        }
    }
}