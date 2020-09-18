package com.lucidity.haolu.lifepointcalculator.view.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.model.Player

@BindingAdapter("styleTextByGainOrLoss")
fun styleTextByGainOrLoss(textView: TextView, lp: Int ) {
    val color = if (lp < 0) R.color.lose else R.color.gain
    val text = String.format("%+d", lp)
    textView.text = text
    textView.setTextColor(ContextCompat.getColor(textView.context, color))
}

@BindingAdapter("styleTextByPlayer")
fun styleTextByPlayer(textView: TextView, player: String) {
    if (player == Player.ONE.tag) {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.yugi_yellow))
    } else {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.yugi_red))
    }
}