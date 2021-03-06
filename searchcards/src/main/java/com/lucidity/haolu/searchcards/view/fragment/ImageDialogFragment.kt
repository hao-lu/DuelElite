package com.lucidity.haolu.searchcards.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import com.lucidity.haolu.searchcards.R

class ImageDialogFragment : DialogFragment() {

    private lateinit var mImageUrl: String
    private var mStatusBarColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = ChangeTransform()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_image_dialog_new, container, false)
        val imageUrl = arguments?.getString("imageUrl")
        mImageUrl = imageUrl!!
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window.statusBarColor = ContextCompat.getColor(context, android.R.color.black)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        val image = view?.findViewById(R.id.image_card) as ImageView
        try {
            Picasso.with(context).load(mImageUrl).into(image)}
        catch (illegalArgument: IllegalArgumentException) {

        }
    }

    // Change the status bar color to black when image view
    override fun onResume() {
        super.onResume()
        val window = activity?.window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        mStatusBarColor = window?.statusBarColor!!
        window.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.black)
    }

    // Change status bar color back
    override fun onPause() {
        super.onPause()
        val window = activity?.window!!
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(context, R.color.colorYugiDarkerBlack)
        window.statusBarColor = mStatusBarColor
    }
}
