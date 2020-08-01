package com.lucidity.haolu.model

import com.squareup.moshi.Json

/**
 * This is the data received while using the YGOdeck api call.
 */

data class CardInfoResponse(
        val data: List<CardInfo>?
)

data class CardInfo(
        val archetype: String?,
        val card_images: List<CardImage>?,
        val card_prices: List<CardPrice>?,
        val card_sets: List<CardSet>?,
        val desc: String?,
        val id: Int?,
        val name: String?,
        val race: String?,
        val type: String?
)

data class CardImage(
        val id: Int?,
        val image_url: String?,
        val image_url_small: String?
)

data class CardPrice(
        val amazon_price: String?,
        val cardmarket_price: String?,
        val coolstuffinc_price: String?,
        val ebay_price: String?,
        val tcgplayer_price: String?
)

data class CardSet(
        val set_code: String?,
        val set_name: String?,
        val set_price: String?,
        val set_rarity: String?,
        val set_rarity_code: String?
)