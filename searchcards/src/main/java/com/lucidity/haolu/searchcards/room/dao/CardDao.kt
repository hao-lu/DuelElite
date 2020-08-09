package com.lucidity.haolu.searchcards.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucidity.haolu.searchcards.room.entity.Card

@Dao
interface CardDao {

    @Query("SELECT * FROM cards")
    suspend fun getAll(): List<String>

    @Query("SELECT * FROM cards WHERE name == :name")
    suspend fun getByName(name: String): Card?

    @Query("SELECT * FROM cards WHERE LOWER(name) LIKE '%'||:substring||'%'")
    suspend fun getAllContainsSubstring(substring: String): List<Card>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: Card)

}