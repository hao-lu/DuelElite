package com.example.haolu.duelmaster

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton

class CoinFlipFragment : DialogFragment() {

    private val TAG = "CoinFlipFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_coin_flip, container, false)
        val image = rootView?.findViewById(R.id.imageButton) as ImageButton
        image.setOnClickListener {
            val images = resources.obtainTypedArray(R.array.coin_images)
            val rand = (Math.random() * images.length()).toInt()
            image.setImageResource(images.getResourceId(rand, R.drawable.poke_ball))
            images.recycle()
        }
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}
