package com.lucidity.haolu.duelking

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton

/**
 * Simulates a dice roll using rand()
 */

class DiceFragment : Fragment() {

    private val TAG = "DiceFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_dice, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val image = view?.findViewById(R.id.image_dice) as ImageButton
        val pulse = AnimationUtils.loadAnimation(context, R.anim.pulse)
        image.setOnClickListener {
            val images = resources.obtainTypedArray(R.array.dice_images)
            val rand = (Math.random() * images.length()).toInt()
            image.setImageResource(images.getResourceId(rand, R.drawable.dice1))
            image.startAnimation(pulse)
            images.recycle()
        }
    }
}