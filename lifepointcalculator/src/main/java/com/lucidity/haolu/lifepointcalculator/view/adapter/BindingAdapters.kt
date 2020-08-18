package com.lucidity.haolu.lifepointcalculator.view.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.lucidity.haolu.lifepointcalculator.R

@BindingAdapter("styleTextByGainOrLoss")
fun styleTextByGainOrLoss(textView: TextView, lp: Int ) {
    val color = if (lp < 0) R.color.lose else R.color.gain
    val text = String.format("%+d", lp)
    textView.text = text
    textView.setTextColor(ContextCompat.getColor(textView.context, color))
}