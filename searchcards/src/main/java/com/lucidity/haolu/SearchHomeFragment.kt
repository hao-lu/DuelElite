package com.lucidity.haolu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        binding.etSearchBoxHint.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.mlSearchHome.transitionToEnd()
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etSearchBoxHint, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun observeSearchBar() {
        viewmodel.searchBar.observe(viewLifecycleOwner, Observer { event ->
//            binding.root.doOnLayout {
//                binding.vSearchBoxBg.layoutParams.height = it.height
//                binding.vSearchBoxBg.layoutParams.width = it.width
//                binding.ivSearchBoxIcon.visibility = View.GONE
//
//                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT )
//                params.bottomToBottom = 0
//                binding.tvSearchBoxHint.layoutParams = params
//                binding.vSearchBoxBg.requestLayout()
//        }
            event.getContentIfNotHandled()?.let {
//                binding.mlSearchHome.transitionToEnd()
                binding.etSearchBoxHint.requestFocus()
//                val imm =
//                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(binding.etSearchBoxHint, InputMethodManager.SHOW_IMPLICIT)
            }

        })
    }




}