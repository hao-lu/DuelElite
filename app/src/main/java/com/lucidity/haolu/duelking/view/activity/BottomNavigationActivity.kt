package com.lucidity.haolu.duelking.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.lifepointcalculator.view.CalculatorFragment

class BottomNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bottom_navigation)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CalculatorFragment())
                .commit()

    }
}