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
import com.lucidity.haolu.searchcards.databinding.FragmentCardInformationBinding
import com.lucidity.haolu.searchcards.view.adapter.CardInformationRecyclerViewAdapter
import com.lucidity.haolu.searchcards.viewmodel.CardInformationViewModel
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardInformationFragment : Fragment() {

    companion object {
        fun newInstance() = CardInformationFragment()
    }

    private lateinit var parentViewModel: SearchCardDetailsViewModel
    private lateinit var viewModel: CardInformationViewModel
    private lateinit var binding: FragmentCardInformationBinding

    private val TAG = "CardInformationFragment"

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = inflater.inflate(R.layout.fragment_card_information, container, false)
//        val cardName = arguments!!.getString("cardName")
//        rootView.findViewById<ProgressBar>(R.id.progressbar_details).visibility = View.VISIBLE
////        ParseDetailsTask(context!!)
////            .execute(cardName)
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireParentFragment()).get(SearchCardDetailsViewModel::class.java)
        viewModel = ViewModelProvider(this).get(CardInformationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_card_information,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.detailsRecyclerView.adapter = DetailsRecyclerViewAdapter(log?.getReverseList() ?: emptyList())
        binding.detailsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        observeProgressBarEvent()
        observeCardInformation()

        // Refactor call once
        if (viewModel.cardInformation.value == null) {
            fetchCardDetails()
        }
    }

    private fun observeProgressBarEvent() {
        viewModel.progressBarEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { visibility ->
                binding.progressbarDetails.isVisible = visibility
            }
        })
    }

    private fun observeCardInformation() {
        viewModel.cardInformation.observe(viewLifecycleOwner, Observer { list ->
            binding.detailsRecyclerView.adapter = CardInformationRecyclerViewAdapter(list ?: emptyList())
        })
    }

    private fun fetchCardDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            parentViewModel.cardName?.run {
                viewModel.fetchCardInformation(this)
            }
        }
    }

}