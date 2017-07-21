package com.example.haolu.duelmaster

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val log = intent.extras.getParcelable<LifePointCalculator.Log>("log")

        val layoutManger = LinearLayoutManager(this)
        val adapter = LogAdapter(log)
        log_recycler_view.layoutManager = layoutManger
        log_recycler_view.adapter = adapter
    }
}
