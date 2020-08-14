package com.lucidity.haolu.searchcards.room.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucidity.haolu.searchcards.generated.callback.OnClickListener

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


data class SearchResultViewState(
        val card: Card,
        val listener: OnClickListener? = null)
