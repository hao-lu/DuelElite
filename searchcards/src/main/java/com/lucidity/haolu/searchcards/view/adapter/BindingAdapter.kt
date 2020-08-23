package com.lucidity.haolu.searchcards.view.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

// TODO: move to base2
@BindingAdapter("bind:backgroundTint")
fun setBackgroundTint(view: View, colorId: Int) {
    val context = view.context
    val tint = ContextCompat.getColorStateList(context, colorId)
    view.backgroundTintList = tint
}