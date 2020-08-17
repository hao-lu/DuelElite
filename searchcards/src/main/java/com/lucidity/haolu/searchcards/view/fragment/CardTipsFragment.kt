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
import com.lucidity.haolu.searchcards.databinding.FragmentCardTipsBinding
import com.lucidity.haolu.searchcards.view.adapter.CardTipsRecyclerViewAdapter
import com.lucidity.haolu.searchcards.viewmodel.CardTipsViewModel
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardTipsFragment : Fragment() {

    companion object {
        fun newInstance() = CardTipsFragment()
    }

    private val TAG = "CardTipsFragment"

    private lateinit var parentViewModel: SearchCardDetailsViewModel
    private lateinit var viewModel: CardTipsViewModel
    private lateinit var binding: FragmentCardTipsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireParentFragment()).get(SearchCardDetailsViewModel::class.java)
        viewModel = ViewModelProvider(this).get(CardTipsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_card_tips,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tipsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))

        observeProgressBarEvent()
        observeCardTips()
        observeEmptyStateCardTipsVisibility()

        if (viewModel.cardTips.value == null) {
            fetchCardTips()
        }
    }

    private fun observeProgressBarEvent() {
        viewModel.progressBarEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { visibility ->
                binding.progressbarTips.isVisible = visibility
            }
        })
    }

    private fun observeCardTips() {
        viewModel.cardTips.observe(viewLifecycleOwner, Observer { list ->
            binding.tipsRecyclerView.adapter = CardTipsRecyclerViewAdapter(list)
        })
    }

    private fun observeEmptyStateCardTipsVisibility() {
        viewModel.emptyStateCardTipsVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            binding.tvNoTipHint.visibility = visibility
            binding.ivNoTip.visibility = visibility
        })
    }

    private fun fetchCardTips() {
        CoroutineScope(Dispatchers.IO).launch {
            parentViewModel.cardName?.run {
                viewModel.fetchCardTips(this)
            }
        }
    }
}