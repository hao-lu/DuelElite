package com.lucidity.haolu.searchcards.view.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

// TODO: refactor not use actual value not id
@BindingAdapter("bind:backgroundTint")
fun setBackgroundTint(view: View, colorId: Int) {
    val context = view.context
    val tint = ContextCompat.getColorStateList(context, colorId)
    view.backgroundTintList = tint
}