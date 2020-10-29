package com.lucidity.haolu.searchcards.room.dao

import androidx.room.*
import com.lucidity.haolu.searchcards.room.entity.RecentSearch

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_searches")
    suspend fun getAll(): List<RecentSearch>

    @Query("SELECT * FROM recent_searches ORDER BY timeStamp DESC LIMIT 5")
    suspend fun getTop5RecentSearch(): List<RecentSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearch: RecentSearch)

//    @Query("DELETE FROM recent_searches WHERE timeStamp = (SELECT MIN(timeStamp) FROM recent_searches)")
    @Query("DELETE FROM recent_searches WHERE timeStamp NOT IN (SELECT timeStamp FROM recent_searches ORDER BY timeStamp DESC LIMIT 5)")
    suspend fun deleteOldest()

}