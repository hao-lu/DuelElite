package com.lucidity.haolu.searchcards.view.fragment

import android.content.Context
import android.os.Bundle
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
import com.lucidity.haolu.base.util.KeyboardUtil
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardResultsBinding
import com.lucidity.haolu.searchcards.transition.RotateCrossfadeTransition
import com.lucidity.haolu.searchcards.view.adapter.OnSearchResultListener
import com.lucidity.haolu.searchcards.view.adapter.SearchCardResultsListAdapter
import com.lucidity.haolu.searchcards.viewmodel.SearchCardResultsViewModel
import kotlinx.coroutines.*

class SearchCardResultsFragment : Fragment(), OnSearchResultListener {

    private lateinit var binding: FragmentSearchCardResultsBinding
    private lateinit var viewmodel: SearchCardResultsViewModel

    companion object {
        fun newInstance() = SearchCardResultsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchCardResultsViewModel::class.java)
        setSharedElementTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_card_results,
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

    override fun onSearchResultClick(position: Int) {
        KeyboardUtil.hideKeyboard(requireActivity())
        val cardName = viewmodel.searchResults.value?.get(position)?.name ?: ""
        val bundle = Bundle()
        bundle.putString("cardName", cardName)
        findNavController().navigate(
            R.id.action_fragment_search_card_to_fragment_search_card_details,
            bundle,
            null,
            null
        )
    }

    private fun observe() {
        viewmodel.backButton.observe(viewLifecycleOwner, Observer {
            KeyboardUtil.hideKeyboard(requireActivity())
            findNavController().popBackStack()
        })
    }

    private fun setSearchBoxOnFocusChangeListener() {
        binding.etSearchBoxHint.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etSearchBoxHint, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun addSearchBoxEditTextChangeListener() {
        binding.etSearchBoxHint.addTextChangedListener { editable ->
            CoroutineScope(Dispatchers.IO).launch {
                delay(200)
                withContext(Dispatchers.Main) {
                    editable?.let { text ->
                        viewmodel.getSearchResult(text.toString())
                        (binding.rvSearchResults.adapter as SearchCardResultsListAdapter).submitList(
                            viewmodel.searchResults.value
                        )
                    }
                }
            }
        }
    }

    private fun setSharedElementTransitions() {
        sharedElementEnterTransition =
            RotateCrossfadeTransition(startRotation = -45f, endRotation = 0f)
        sharedElementReturnTransition =
            RotateCrossfadeTransition(
                true,
                0f,
                45f
            )
    }

    private fun setupSearchCardRecyclerView() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = SearchCardResultsListAdapter(this)
    }

}