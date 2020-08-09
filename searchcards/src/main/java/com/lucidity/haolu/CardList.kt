package com.lucidity.haolu

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucidity.haolu.generated.callback.OnClickListener

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


data class SearchViewState(
        val card: String,
        val onClickListener: OnClickListener? = null
)