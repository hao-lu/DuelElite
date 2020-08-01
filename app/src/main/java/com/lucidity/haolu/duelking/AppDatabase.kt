package com.lucidity.haolu.duelking

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucidity.haolu.Card
import com.lucidity.haolu.CardDao

@Database(entities = arrayOf(Card::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}