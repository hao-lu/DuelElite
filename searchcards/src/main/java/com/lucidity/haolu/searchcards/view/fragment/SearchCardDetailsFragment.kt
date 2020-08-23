package com.lucidity.haolu.searchcards.view.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.transition.ChangeBounds
import com.google.android.material.tabs.TabLayoutMediator
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardDetailsBinding
import com.lucidity.haolu.searchcards.util.Constants
import com.lucidity.haolu.searchcards.view.adapter.SearchCardDetailsViewPagerAdapter
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModelFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCardDetailsFragment : Fragment() {

    companion object {
        fun newInstance(): SearchCardDetailsFragment {
            val args = Bundle()
            val fragment = SearchCardDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewPagerAdapter: SearchCardDetailsViewPagerAdapter
    private lateinit var binding: FragmentSearchCardDetailsBinding
    private lateinit var viewmodel: SearchCardDetailsViewModel

    private var cardName: String? = null

    private val picassoTargetAccessoryColor = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

        override fun onBitmapFailed(errorDrawable: Drawable?) {}

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                    imageHeader.setImageBitmap(bitmap)
            val swatch = Palette.from(bitmap!!).setRegion(25, 25, 35, 35).generate()
            val dominant = swatch.dominantSwatch
//            if (dominant == null) Log.d(TAG, "DOMINANT NULL")
            if (dominant != null) {
                binding.collapseToolbar.setContentScrimColor(dominant.rgb)
                binding.tabs.setSelectedTabIndicatorColor(dominant.rgb)
                activity?.window?.statusBarColor = dominant.rgb
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewmodel = ViewModelProvider(this).get(SearchCardDetailsViewModel::class.java)
        cardName = arguments?.getString(Constants.BUNDLE_KEY_CARD_NAME)
        viewmodel = ViewModelProvider(this, SearchCardDetailsViewModelFactory(cardName)).get(
            SearchCardDetailsViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_card_details,
            container,
            false
        )
        binding.viewmodel = viewmodel
        val transitionName = arguments?.getString("transitionName")
        binding.mainContent.transitionName = transitionName
        sharedElementEnterTransition = ChangeBounds()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupToolbar()

        observeCardImageUrl()
        fetchCardImageUrl()
    }

    private fun setupToolbar() {
        binding.toolbar.title = cardName
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }
    }

    private fun setupViewPager() {
        viewPagerAdapter =
            SearchCardDetailsViewPagerAdapter(
                this
            )
        binding.container.adapter = viewPagerAdapter
        val viewPager2 = binding.container

        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.details_hao)
                1 -> getString(R.string.rulings_hao)
                else -> getString(R.string.tips_hao)
            }
        }.attach()
    }

    private fun fetchCardImageUrl() {
        CoroutineScope(Dispatchers.IO).launch {
//            val cardName = arguments?.getString("cardName")
//            val url = "https://yugioh.wikia.com/wiki/" + cardName!!.encodeToHtmlAndReplacePlusWithUnderscore()
            cardName?.run {
                viewmodel.fetchCardImageUrl(this)
            }
        }
    }

    private fun observeCardImageUrl() {
        viewmodel.imageUrl.observe(viewLifecycleOwner, Observer { imageUrl ->
            Picasso.with(context).load(imageUrl).into(binding.imageHeader)
//            Picasso.with(context).load(imageUrl).into(picassoTargetAccessoryColor)
            binding.imageHeader.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("imageUrl", imageUrl)
                findNavController().navigate(
                    R.id.action_fragment_search_card_to_fragment_image_dialog,
                    bundle,
                    null,
                    null
                )
            }
        })
    }
}