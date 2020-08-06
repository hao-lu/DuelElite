package com.lucidity.haolu.view

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
import com.lucidity.haolu.R
import com.lucidity.haolu.viewmodel.SearchHomeViewModel
import com.lucidity.haolu.databinding.FragmentSearchHomeBinding


class SearchHomeFragment : Fragment() {

    private lateinit var binding: FragmentSearchHomeBinding
    private lateinit var viewmodel: SearchHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchHomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_home,
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
            binding.ivIcon to "iconTransition" ,
            binding.vSearchBoxBg to "searchBoxTransition"
        )
        findNavController().navigate(
            R.id.action_fragment_search_home_to_fragment_search,
            null,
            null,
            extras
        )
    }

}