package com.lucidity.haolu.searchcards.room.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucidity.haolu.searchcards.R
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
    @PrimaryKey val name: String
)

data class SearchResultViewState(
    val card: Card,
    val listener: OnClickListener? = null
)

@Entity(tableName = "recent_searches")
data class RecentSearch(
    @NonNull
    @PrimaryKey val name: String,
    val color: Int,
    val timeStamp: Long
)

enum class CardType(val color: Int) {
    MONSTER(R.color.monster_tan),
    SPELL(R.color.spell_green),
    TRAP(R.color.trap_violet),
    UNKNOWN(R.color.gray_background)
}