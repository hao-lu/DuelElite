package com.lucidity.haolu.duelking.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import com.lucidity.haolu.duelking.R

/**
 * Simulates a coin flip with rand()
 */

class CoinFragment : Fragment() {

    private val TAG = "CoinFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_coin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val image = view?.findViewById(R.id.image_coin) as ImageButton
        val pulse = AnimationUtils.loadAnimation(context, R.anim.pulse)
        image.setOnClickListener {
            val images = resources.obtainTypedArray(R.array.coin_images)
            val rand = (Math.random() * images.length()).toInt()
            image.setImageResource(images.getResourceId(rand, R.drawable.coin_head))
            image.startAnimation(pulse)
            images.recycle()
        }
    }

}

