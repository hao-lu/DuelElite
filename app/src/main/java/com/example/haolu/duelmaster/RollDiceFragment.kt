package com.example.haolu.duelmaster
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton

class RollDiceFragment : DialogFragment() {

    private val TAG = "RollDiceFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_roll_dice, container, false)
        val image = rootView?.findViewById(R.id.imageButton) as ImageButton
        image.setOnClickListener {
            val images = resources.obtainTypedArray(R.array.dice_images)
            val rand = (Math.random() * images.length()).toInt()
            image.setImageResource(images.getResourceId(rand, R.drawable.dice1))
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
