package com.lucidity.haolu.searchcards.room.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Used to search card names locally
 */

data class CardList(
        val data: List<String>?
)

@Entity(tableName = "cards")
data class Card(
        @NonNull
        @PrimaryKey val name: String)

