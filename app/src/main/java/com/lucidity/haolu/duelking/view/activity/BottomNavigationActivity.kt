package com.lucidity.haolu.duelking.view.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.databinding.ActivityBottomNavigationBinding
import com.lucidity.haolu.duelking.view.BottomNavigationViewModel
import com.lucidity.haolu.lifepointcalculator.view.CalculatorFragment
import com.lucidity.haolu.lifepointcalculator.view.LogFragment

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var viewmodel: BottomNavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_bottom_navigation
        )

        viewmodel = ViewModelProvider(this).get(BottomNavigationViewModel::class.java)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CalculatorFragment.newInstance())
            .addToBackStack(null)
            .commit()

        if (Build.VERSION.SDK_INT >= 27) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        else {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.yugi_black)
        }

        setBottomNavigationItemListeners()
    }

    private fun setBottomNavigationItemListeners() {
        binding.bottomNavigationMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_calculator -> {
                    supportFragmentManager.popBackStack()
                    showCalculatorFragment()
                    true
                }
                R.id.navigation_random -> {
                    supportFragmentManager.popBackStack()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LogFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.navigation_search -> {
                    supportFragmentManager.popBackStack()
                    true
                }
                R.id.navigation_settings -> {
                    supportFragmentManager.popBackStack()
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationMain.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.navigation_calculator -> {
                    true
                }
                R.id.navigation_random -> {
                    true
                }
                R.id.navigation_search -> {
                    true
                }
                R.id.navigation_settings -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun showCalculatorFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CalculatorFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}