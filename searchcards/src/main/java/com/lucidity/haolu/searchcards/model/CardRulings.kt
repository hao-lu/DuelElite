package com.lucidity.haolu.searchcards.model

class CardRulings(
    val rulings: ArrayList<HeaderOrItem>) {

    fun addHeader2(text: String) {
        rulings.add(HeaderOrItem(HeaderOrItem.Types.H2, text))
    }

    fun addHeader3(text: String) {
        rulings.add(HeaderOrItem(HeaderOrItem.Types.H3, text))
    }

    fun addDiv(text: String) {
        rulings.add(HeaderOrItem(HeaderOrItem.Types.DIV, text))
    }

    fun addUl(text: String) {
        rulings.add(HeaderOrItem(HeaderOrItem.Types.UL, text))
    }
}

data class HeaderOrItem(val type: Types, val data: String) {

    enum class Types {
        H2,
        H3,
        TABLE,
        DIV,
        UL
    }

}