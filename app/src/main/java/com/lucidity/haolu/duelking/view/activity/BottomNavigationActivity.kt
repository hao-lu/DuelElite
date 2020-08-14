package com.lucidity.haolu.duelking.view.activity

import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.internal.ViewUtils.getContentView
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.databinding.ActivityBottomNavigationBinding
import com.lucidity.haolu.duelking.setupWithNavController
import com.lucidity.haolu.duelking.view.BottomNavigationViewModel


class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var viewmodel: BottomNavigationViewModel

    private var isKeyboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 27) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // or
//                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR //or
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
//            window.navigationBarColor = Color.WHITE
        } else {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.yugi_black)
        }

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_bottom_navigation
        )
        viewmodel = ViewModelProvider(this).get(BottomNavigationViewModel::class.java)

        val navGraphIds = listOf(R.navigation.navigation_calculator, R.navigation.navigation_random, R.navigation.navigation_search)
        binding.bottomNavigationMain.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.nav_host_container,
            intent
        )

        // Only have BottomNavigationView handle fitsSystemWindow
//        binding.clActivityMain.setOnApplyWindowInsetsListener(null)

      setupKeyboardListener()
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