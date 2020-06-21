package com.example.randomgenerator

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.MotionEventCompat
import androidx.fragment.app.Fragment

class CoinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_coin_new, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ARG_OBJECT") }?.apply {
//            val textView: TextView = view.findViewById(R.id.tv_title)
//            textView.text = getInt("ARG_OBJECT").toString()
//        }

        val icon = view.findViewById<ImageView>(R.id.icon)
        val startY = icon.translationY
        val animator = ValueAnimator.ofFloat(startY, startY - 1000).apply {
            interpolator = DecelerateInterpolator()
            duration = 500
            addUpdateListener {
                val v = it.animatedValue as Float
                icon.translationY = v

                doOnEnd {
                    val topY = icon.translationY
                    val flipDown = ValueAnimator.ofFloat(topY, topY + 1000).apply {
                        interpolator = DecelerateInterpolator()
                        duration = 500
                        addUpdateListener { value->
                            val v2 = value.animatedValue as Float
                            icon.translationY = v2
                        }
                    }
                    flipDown.start()
                }
            }
        }

        val flip = 180
        val numOfFlip = 8f
        val rotations = flip * numOfFlip
        val rotation = ValueAnimator.ofFloat(0f, rotations).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1000
            addUpdateListener {
                val v = it.animatedValue as Float
                icon.rotationX = v
            }
        }

        val topY = icon.translationY
        val flipDown = ValueAnimator.ofFloat(topY, topY + 1000).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1000
            addUpdateListener {
                val v = it.animatedValue as Float
                icon.translationY = v
            }
        }

        icon.setOnClickListener {
            animator.start()
            rotation.start()
        }
    }

//    private fun normalize(f: Float): Float {
//
//    }

}