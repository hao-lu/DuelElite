package com.lucidity.haolu.lifepointcalculator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucidity.haolu.lifepointcalculator.LogRecyclerViewAdapter
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentLogBinding
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.lucidity.haolu.lifepointcalculator.model.LifePointLog


class LogFragment : Fragment() {

    private lateinit var binding: FragmentLogBinding

    companion object {
        fun newInstance() = LogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_log,
            container,
            false
        )
        val log = arguments?.getParcelable<LifePointLog>("LOG_BUNDLE_KEY")
        binding.rvLog.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvLog.adapter = LogRecyclerViewAdapter(dummyList())
        binding.rvLog.adapter = LogRecyclerViewAdapter(log?.getReverseList() ?: emptyList())
        binding.rvLog.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        binding.toolbar.title = getString(R.string.title_log)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
//            activity?.supportFragmentManager?.popBackStack()
            findNavController(this).popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val log = arguments?.getParcelable<LifePointLog>("LOG_BUNDLE_KEY")
    }

    private fun dummyList(): List<LifePointLogItem> {
        val list = mutableListOf<LifePointLogItem>()
        list.add(LifePointLogItem("You",  -1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  -1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  -1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  -1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  -1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  1000, 2000, "34:20"))
        list.add(LifePointLogItem("You",  1000, 2000, "34:20"))
        return list
    }
}