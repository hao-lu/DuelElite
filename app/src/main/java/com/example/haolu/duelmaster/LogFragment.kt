package com.example.haolu.duelmaster

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_log.*
import kotlinx.android.synthetic.main.activity_main.*

class LogFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater!!.inflate(R.layout.fragment_log, container, false)
        val layoutManger = LinearLayoutManager(activity)
        val adapter = LogAdapter((activity as MainActivity).LIFE_POINT_CALCULATOR.log)
        val recyclerView = rootView.findViewById(R.id.log_recycler_view) as RecyclerView

        recyclerView.layoutManager = layoutManger
        recyclerView.adapter = adapter
//        return inflater!!.inflate(R.layout.fragment_log, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }
}