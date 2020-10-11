package com.lucidity.haolu.searchcards.view.fragment

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lucidity.haolu.searchcards.viewmodel.SearchCardHomeViewModel
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.SearchCardsDatabase
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardHomeBinding
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.room.entity.CardList
import com.lucidity.haolu.searchcards.util.Constants
import com.lucidity.haolu.searchcards.view.adapter.OnRecentSearchListener
import com.lucidity.haolu.searchcards.view.adapter.RecentSearchesRecyclerViewAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.io.IOException

class SearchCardHomeFragment : Fragment(), OnRecentSearchListener {

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var binding: FragmentSearchCardHomeBinding
    private lateinit var viewmodel: SearchCardHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: populate only once
        populateSearchCardsDatabase()
        viewmodel = ViewModelProvider(this).get(SearchCardHomeViewModel::class.java)
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
        binding.rvRecentSearches.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSearchBar()
        observeRecentSearchList()

        // TODO: add scoping
        CoroutineScope(Dispatchers.IO).launch {
            viewmodel.updateRecentSearchList()
        }
    }

    override fun onRecentResultClick(position: Int) {
//        val bundle = Bundle()
//        val cardName = viewmodel.recentSearchList.value?.get(position)?.name ?: ""
//        bundle.putString("cardName", cardName)
//        navigateToSearchCardDetailsFragment(bundle)
    }

    override fun onRecentResultClick(position: Int, view: View) {
        if (isNetworkAvailable()) {
            val bundle = Bundle()
            val cardName = viewmodel.recentSearchList.value?.get(position)?.name ?: ""
            bundle.putString(Constants.BUNDLE_KEY_CARD_NAME, cardName)
            bundle.putString("transitionName", view.transitionName)
            val extras = FragmentNavigatorExtras(
                view to view.transitionName
            )
            findNavController().navigate(
                R.id.action_fragment_search_home_to_fragment_search_card_details,
                bundle,
                null,
                extras)
        } else {
            showNoInternetConnectionSnackbar()
        }
    }

    private fun observeSearchBar() {
        viewmodel.searchBar.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (isNetworkAvailable()) {
                    navigateToSearchFragment()
                } else {
                    showNoInternetConnectionSnackbar()
                }
            }
        })
    }

    private fun observeRecentSearchList() {
        viewmodel.recentSearchList.observe(viewLifecycleOwner, Observer { list ->
            binding.rvRecentSearches.adapter = RecentSearchesRecyclerViewAdapter(list ?: emptyList(), this)
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

    private fun navigateToSearchCardDetailsFragment(bundle: Bundle) {
        val extras = FragmentNavigatorExtras(
            binding.vSearchBoxBg to "searchBoxTransition"
        )
        findNavController().navigate(
            R.id.action_fragment_search_home_to_fragment_search_card_details,
            bundle,
            null,
            extras)
    }

    private fun populateSearchCardsDatabase() {
        val db = SearchCardsDatabase.getInstance(requireContext())
        scope.launch {
            loadJsonFromRawResource(R.raw.database)?.let { json ->
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val cardAdapter = moshi.adapter<CardList>(
                    CardList::class.java)
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

    private fun showNoInternetConnectionSnackbar() {
        Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_SHORT)
            .setActionTextColor(Color.WHITE)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .show()
    }

    // Move to network util
    private fun isNetworkAvailable(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

}