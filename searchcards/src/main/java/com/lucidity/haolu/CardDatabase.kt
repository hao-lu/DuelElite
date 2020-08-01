package com.lucidity.haolu

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Card::class), version = 1)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardHaoDao(): CardDao
}