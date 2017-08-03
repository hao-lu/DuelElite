package com.example.haolu.duelmaster

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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

        val log = intent.extras.getParcelable<LifePointCalculator.Log>("log")

        val layoutManger = LinearLayoutManager(this)
        // Puts the recent log on top (stack)
        layoutManger.reverseLayout = true
        layoutManger.stackFromEnd = true
        val adapter = LogAdapter(log)
        log_recycler_view.layoutManager = layoutManger
        log_recycler_view.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
