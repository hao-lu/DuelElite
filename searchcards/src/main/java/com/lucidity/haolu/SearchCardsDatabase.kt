package com.lucidity.haolu

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Card::class), version = 1)
abstract class SearchCardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

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
            return Room.databaseBuilder(
                context,
                SearchCardsDatabase::class.java, "searchCardsDb"
            )
                .build()
        }
    }
}