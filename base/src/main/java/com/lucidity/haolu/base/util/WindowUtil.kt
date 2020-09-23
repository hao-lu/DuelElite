package com.lucidity.haolu.base.util

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.lucidity.haolu.base.R

class WindowUtil {

    companion object {

        // Extension?
        fun setStatusBarColor(activity: Activity) {
            activity.window.statusBarColor = Color.WHITE
        }

        fun setNavigationBarColor(activity: Activity) {
            activity.window.navigationBarColor = Color.WHITE
        }

        fun setStatusBarTransparent(activity: Activity) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }

        fun setStatusBarColorByTheme(activity: Activity) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.night_color_surface)
            } else {
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.color_surface)
            }
        }
    }
}