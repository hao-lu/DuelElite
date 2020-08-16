package com.lucidity.haolu.searchcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchCardDetailsViewModelFactory(val cardName: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchCardDetailsViewModel(cardName) as T
    }
}