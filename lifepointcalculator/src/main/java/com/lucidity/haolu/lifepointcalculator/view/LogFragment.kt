package com.lucidity.haolu.lifepointcalculator.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucidity.haolu.lifepointcalculator.LogRecyclerViewAdapter
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentLogBinding
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration


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
        binding.rvLog.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLog.adapter = LogRecyclerViewAdapter(dummyList())
        binding.rvLog.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        binding.toolbar.title = getString(R.string.title_log)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        return binding.root
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