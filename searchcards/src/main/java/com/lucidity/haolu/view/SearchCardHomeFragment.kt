package com.lucidity.haolu.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.lucidity.haolu.*
import com.lucidity.haolu.viewmodel.SearchCardHomeViewModel
import com.lucidity.haolu.databinding.FragmentSearchCardHomeBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.URLEncoder
import java.net.UnknownHostException


class SearchCardHomeFragment : Fragment() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var binding: FragmentSearchCardHomeBinding
    private lateinit var viewmodel: SearchCardHomeViewModel

//    val db = SearchCardsDatabase.getInstance(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchCardHomeViewModel::class.java)

        populateSearchCardsDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_card_home,
            container,
            false
        )
        binding.viewmodel = viewmodel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSearchBar()
    }

    private fun observeSearchBar() {
        viewmodel.searchBar.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                navigateToSearchFragment()
            }
        })
    }

    private fun navigateToSearchFragment() {
        val extras = FragmentNavigatorExtras(
            binding.ivIcon to "iconTransition",
            binding.vSearchBoxBg to "searchBoxTransition"
        )
        findNavController().navigate(
            R.id.action_fragment_search_home_to_fragment_search,
            null,
            null,
            extras
        )
    }

    private fun populateSearchCardsDatabase() {
        val db = SearchCardsDatabase.getInstance(requireContext())
        scope.launch {
            loadJsonFromRawResource(R.raw.database)?.let { json ->
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val cardAdapter = moshi.adapter<CardList>(CardList::class.java)
                withContext(Dispatchers.IO) {
                    val cardList = cardAdapter.fromJson(json)
                    cardList?.data?.let { list ->
                        for (card in list) {
                            db?.cardDao()?.insert(Card(card))
                        }
                    }
                }
            }
        }

    }

    private fun loadJsonFromRawResource(rawSourceId: Int): String? {
        return try {
            val inputStream = resources.openRawResource(rawSourceId)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}