package com.lucidity.haolu.searchcards.view.fragment

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
import com.lucidity.haolu.searchcards.viewmodel.SearchCardHomeViewModel
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.SearchCardsDatabase
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardHomeBinding
import com.lucidity.haolu.searchcards.room.entity.Card
import com.lucidity.haolu.searchcards.room.entity.CardList
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
        val bundle = Bundle()
        val cardName = viewmodel.recentSearchList.value?.get(position)?.name ?: ""
        bundle.putString("cardName", cardName)
        findNavController().navigate(
            R.id.action_fragment_search_home_to_fragment_search_card_details,
            bundle,
            null,
            null
        )
    }

    private fun observeSearchBar() {
        viewmodel.searchBar.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                navigateToSearchFragment()
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

}