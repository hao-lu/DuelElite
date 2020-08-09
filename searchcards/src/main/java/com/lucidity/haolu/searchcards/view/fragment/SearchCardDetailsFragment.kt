package com.lucidity.haolu.searchcards.view.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import com.lucidity.haolu.searchcards.view.adapter.SearchCardDetailsViewPagerAdapter
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardDetailsBinding
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModelFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.*
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class SearchCardDetailsFragment : Fragment() {

    private lateinit var viewPagerAdapter: SearchCardDetailsViewPagerAdapter
    private lateinit var binding: FragmentSearchCardDetailsBinding
    private lateinit var viewmodel: SearchCardDetailsViewModel

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
        val cardName = arguments?.getString("cardName")!!
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
        val cardName = arguments?.getString("cardName")
        binding.toolbar.title = cardName
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }
    }

    private fun setupViewPager() {
        val cardName = arguments?.getString("cardName")
        viewPagerAdapter =
            SearchCardDetailsViewPagerAdapter(
                this,
                cardName!!
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
            val cardName = arguments?.getString("cardName")
//            val url = "https://yugioh.wikia.com/wiki/" + cardName!!.encodeToHtmlAndReplacePlusWithUnderscore()
            val imageUrl = viewmodel.fetchCardImageUrl(cardName!!)
            println(imageUrl)
        }
    }

    private fun observeCardImageUrl() {
        viewmodel.imageUrl.observe(viewLifecycleOwner, Observer { imageUrl ->
            Picasso.with(context).load(imageUrl).into(binding.imageHeader)
            Picasso.with(context).load(imageUrl).into(picassoTargetAccessoryColor)
        })
    }
}