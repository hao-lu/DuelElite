package com.lucidity.haolu.duelking.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.databinding.ActivityBottomNavigationBinding
import com.lucidity.haolu.lifepointcalculator.view.CalculatorFragment

class BottomNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityBottomNavigationBinding>(
            this,
            R.layout.activity_bottom_navigation
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CalculatorFragment())
            .commit()

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}