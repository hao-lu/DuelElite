package com.lucidity.haolu.duelking.view.adapter

import com.squareup.picasso.Picasso
import androidx.databinding.BindingAdapter
import android.widget.ImageView

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    if (url != "") {
        Picasso.with(imageView.context).load(url).into(imageView)
    }
}