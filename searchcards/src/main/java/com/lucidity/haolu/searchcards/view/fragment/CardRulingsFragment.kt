package com.lucidity.haolu.searchcards.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.databinding.FragmentCardRulingsBinding
import com.lucidity.haolu.searchcards.view.adapter.CardRulingsRecyclerViewAdapter
import com.lucidity.haolu.searchcards.viewmodel.CardRulingsViewModel
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardRulingsFragment : Fragment() {

    companion object {
        fun newInstance() = CardRulingsFragment()
    }

    private val TAG = "CardRulingsFragment"
    private lateinit var parentViewModel: SearchCardDetailsViewModel
    private lateinit var viewModel: CardRulingsViewModel
    private lateinit var binding: FragmentCardRulingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireParentFragment()).get(SearchCardDetailsViewModel::class.java)
        viewModel = ViewModelProvider(this).get(CardRulingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_card_rulings,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRulings.layoutManager = LinearLayoutManager(requireContext())

        observeProgressBarEvent()
        observeCardRulings()

        if (viewModel.cardRulings.value == null) {
            fetchCardRulings()
        }
    }

    private fun observeProgressBarEvent() {
        viewModel.progressBarEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { visibility ->
                binding.progressbarRulings.isVisible = visibility
            }
        })
    }

    private fun observeCardRulings() {
        viewModel.cardRulings.observe(viewLifecycleOwner, Observer { list ->
            binding.rvRulings.adapter = CardRulingsRecyclerViewAdapter(list)
        })
    }

    private fun fetchCardRulings() {
        CoroutineScope(Dispatchers.IO).launch {
            parentViewModel.cardName?.run {
                viewModel.fetchCardRulings(this)
            }
        }
    }

}