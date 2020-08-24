package com.lucidity.haolu.searchcards.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.databinding.FragmentFullScreenImageViewerBinding
import com.squareup.picasso.Picasso

class FullScreenImageViewerFragment : Fragment() {

    private lateinit var binding: FragmentFullScreenImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_full_screen_image_viewer,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val imageUrl = arguments?.getString("imageUrl")
            Picasso.with(context).load(imageUrl).into(binding.ivImage)
        }
        catch (illegalArgument: IllegalArgumentException) {

        }
    }
}