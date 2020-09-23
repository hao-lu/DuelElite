package com.lucidity.haolu.duelking.view.activity

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.databinding.ActivityBottomNavigationBinding
import com.lucidity.haolu.duelking.setupWithNavController
import com.lucidity.haolu.duelking.view.BottomNavigationViewModel


class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var viewmodel: BottomNavigationViewModel

    private var currentNavController: LiveData<NavController>? = null
    private var isKeyboardVisible = false

    private val lightSystemUiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    private val darkSystemUiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.App_LightTheme)
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 27) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                window.decorView.systemUiVisibility = darkSystemUiFlags
            } else {
                window.decorView.systemUiVisibility = lightSystemUiFlags
            }
        } else {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.yugi_black)
        }

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_bottom_navigation
        )
        viewmodel = ViewModelProvider(this).get(BottomNavigationViewModel::class.java)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        // Only have BottomNavigationView handle fitsSystemWindow
//        binding.clActivityMain.setOnApplyWindowInsetsListener(null)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                recreate()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                recreate()
            }
        }
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.navigation_calculator, R.navigation.navigation_random, R.navigation.navigation_search)
        val controller = binding.bottomNavigationMain.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.nav_host_container,
            intent
        )
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun checkSystemNavigation() {
        binding.clActivityMain.setOnApplyWindowInsetsListener { v, insets ->
            if (insets.systemWindowInsetBottom < 100) {
                if (Build.VERSION.SDK_INT >= 27) {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    window.navigationBarColor = Color.TRANSPARENT
                }
            } else {
                // TODO: check theme
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            insets
        }
    }

    private fun setupKeyboardListener() {
        // Move to util
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rectangle = Rect()
            //error getContentView
            binding.root.rootView.getWindowVisibleDisplayFrame(rectangle)
            val screenHeight = binding.root.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // If keypad is shown, the rectangle.bottom is smaller than that before.
            val keypadHeight = screenHeight - rectangle.bottom
            // 0.15 ratio is perhaps enough to determine keypad height.
            val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15
            if (isKeyboardVisible != isKeyboardNowVisible) {
                if (isKeyboardNowVisible) {
                    binding.bottomNavigationMain.visibility = View.GONE
                } else {
                    binding.bottomNavigationMain.visibility = View.VISIBLE
                }
            }
            isKeyboardVisible = isKeyboardNowVisible

        }
    }
}