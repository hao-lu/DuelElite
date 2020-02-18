package com.lucidity.haolu.lifepointcalculator

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("styleTextByGainOrLoss")
fun styleTextByGainOrLoss(textView: TextView, lp: Int ) {
    val color = if (lp < 0) R.color.colorLose else R.color.colorGain
    val text = String.format("%+d", lp)
    textView.text = text
    textView.setTextColor(ContextCompat.getColor(textView.context, color))
}