package com.lucidity.haolu.searchcards.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardInformationFragment : Fragment() {

    private lateinit var binding: FragmentCardInformationBinding
    private lateinit var viewmodel: CardInformationViewModel

    private val TAG = "DetailsFragment"

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
        viewmodel = ViewModelProvider(this).get(CardInformationViewModel::class.java)
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

        observeCardDetails()

        fetchCardDetails()
    }

    private fun observeCardDetails() {
        viewmodel.cardDetails.observe(viewLifecycleOwner, Observer {  list ->
            binding.detailsRecyclerView.adapter = CardInformationRecyclerViewAdapter(list ?: emptyList())
        })
    }

    private fun fetchCardDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val cardName = arguments?.getString("cardName")
            viewmodel.fetchCardDetails(cardName!!)
        }
    }

}