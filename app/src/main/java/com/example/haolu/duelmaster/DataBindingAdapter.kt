package com.example.haolu.duelmaster

import com.squareup.picasso.Picasso
import android.databinding.BindingAdapter
import android.widget.ImageView

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    if (url != "") {
        Picasso.with(imageView.context).load(url).into(imageView)
    }
}