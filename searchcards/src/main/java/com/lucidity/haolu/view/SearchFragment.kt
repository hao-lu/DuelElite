package com.lucidity.haolu.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lucidity.haolu.R
import com.lucidity.haolu.databinding.FragmentSearchBinding
import com.lucidity.haolu.transition.RotateCrossfadeTransition
import com.lucidity.haolu.viewmodel.SearchHomeViewModel
import com.lucidity.haolu.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewmodel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchViewModel::class.java)
        setSharedElementTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        binding.viewmodel = viewmodel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setSearchBoxOnFocusChangeListener()
        binding.etSearchBoxHint.requestFocus()
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

}