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
import com.lucidity.haolu.searchcards.databinding.FragmentDetailsNewBinding
import com.lucidity.haolu.searchcards.view.adapter.DetailsRecyclerViewAdapter
import com.lucidity.haolu.searchcards.viewmodel.DetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsNewBinding
    private lateinit var viewmodel: DetailsViewModel

    private val TAG = "DetailsFragment"

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = inflater.inflate(R.layout.fragment_details_new, container, false)
//        val cardName = arguments!!.getString("cardName")
//        rootView.findViewById<ProgressBar>(R.id.progressbar_details).visibility = View.VISIBLE
////        ParseDetailsTask(context!!)
////            .execute(cardName)
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_details_new,
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
            binding.detailsRecyclerView.adapter = DetailsRecyclerViewAdapter(list ?: emptyList())
        })
    }

    private fun fetchCardDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val cardName = arguments?.getString("cardName")
            viewmodel.fetchCardDetails(cardName!!)
        }
    }

}