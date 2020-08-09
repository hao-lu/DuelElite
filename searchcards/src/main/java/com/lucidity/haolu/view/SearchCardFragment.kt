package com.lucidity.haolu.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucidity.haolu.Card
import com.lucidity.haolu.R
import com.lucidity.haolu.SearchCardRecyclerViewAdapter
import com.lucidity.haolu.databinding.FragmentSearchCardBinding
import com.lucidity.haolu.transition.RotateCrossfadeTransition
import com.lucidity.haolu.viewmodel.SearchCardViewModel
import kotlinx.coroutines.*

class SearchCardFragment : Fragment(), SearchCardRecyclerViewAdapter.OnSearchResultListener {

    private lateinit var binding: FragmentSearchCardBinding
    private lateinit var viewmodel: SearchCardViewModel

//    private val listener = object: SearchCardRecyclerViewAdapter.OnSearchResultListener {
//        override fun onSearchResultClick(position: Int) {
//            TODO("Not yet implemented")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchCardViewModel::class.java)
        setSharedElementTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_card,
            container,
            false
        )
        binding.viewmodel = viewmodel
        setupSearchCardRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setSearchBoxOnFocusChangeListener()
        addSearchBoxEditTextChangeListener()
        binding.etSearchBoxHint.requestFocus()
    }

    // TODO: refactor
    override fun onSearchResultClick(card: Card) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchBoxHint.windowToken, 0)
        val bundle = Bundle()
        bundle.putString("cardName", card.name)
        findNavController().navigate(
            R.id.action_fragment_search_card_to_fragment_search_card_details,
            bundle,
            null,
            null
        )
    }

    private fun observe() {
        viewmodel.backButton.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack()
        })
    }

    private fun setSearchBoxOnFocusChangeListener() {
        binding.etSearchBoxHint.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etSearchBoxHint, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun addSearchBoxEditTextChangeListener() {
        binding.etSearchBoxHint.addTextChangedListener { editable ->
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
//                viewmodel.getSearchResults(editable.toString())
                withContext(Dispatchers.Main) {
                    val list = viewmodel.getSearchResult(editable.toString())
                    binding.rvSearchResults.adapter = SearchCardRecyclerViewAdapter(list, this@SearchCardFragment)
                }
            }
        }
    }

    private fun setSharedElementTransitions() {
        sharedElementEnterTransition =
            RotateCrossfadeTransition()
        sharedElementReturnTransition =
            RotateCrossfadeTransition(
                true,
                0f,
                45f
            )
    }

    private fun setupSearchCardRecyclerView() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvSearchResults.adapter = SearchCardRecyclerViewAdapter(listOf("Blue-Eyes White Dragon"))
    }

}