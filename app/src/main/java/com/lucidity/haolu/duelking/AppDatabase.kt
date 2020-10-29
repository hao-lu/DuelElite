package com.lucidity.haolu.duelking

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.room.dao.CardDao

//@Database(entities = arrayOf(Card::class), version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun cardDao(): CardDao
//
//    companion object {
//        var instance: AppDatabase? = null
//        fun getInstance(context: Context): AppDatabase? {
//            if (instance == null) {
//                synchronized(AppDatabase::class.java) {
//                    if (instance == null) {
//                        instance =
//                            buildDataBaseBuild(context)
//                    }
//                }
//            }
//            return instance
//        }
//
//        private fun buildDataBaseBuild(context: Context): AppDatabase {
//            return Room.databaseBuilder(
//                context,
//                AppDatabase::class.java, "appdb"
//            )
//                .build()
//        }
//    }
//}
