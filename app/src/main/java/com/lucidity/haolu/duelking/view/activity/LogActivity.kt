package com.lucidity.haolu.duelking.view.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.model.LifePointCalculator
import com.lucidity.haolu.duelking.view.adapter.LogRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        setSupportActionBar(toolbar)
        // Remove title from toolbar
//        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Enable and show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val log = intent.extras.getParcelable<LifePointCalculator.Log>("mLog")

        val layoutManger = LinearLayoutManager(this)
        // Puts the recent mLog on top (stack)
        layoutManger.reverseLayout = true
        layoutManger.stackFromEnd = true
        val adapter = LogRecyclerViewAdapter(log)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        log_recycler_view.addItemDecoration(itemDecoration)
        log_recycler_view.layoutManager = layoutManger
        log_recycler_view.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
