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
import com.lucidity.haolu.duelking.setupWithNavController
import com.lucidity.haolu.duelking.view.BottomNavigationViewModel

class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var viewmodel: BottomNavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 27) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // or
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.yugi_black)
        }

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_bottom_navigation
        )
        viewmodel = ViewModelProvider(this).get(BottomNavigationViewModel::class.java)

        val navGraphIds = listOf(R.navigation.navigation_calculator, R.navigation.navigation_random)
        binding.bottomNavigationMain.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.nav_host_container,
            intent
        )
    }
}