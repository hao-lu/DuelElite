package com.lucidity.haolu.base.util

import android.app.Activity
import android.graphics.Color

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

    }
}