package com.example.randomgenerator.view.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.randomgenerator.R
import com.example.randomgenerator.databinding.FragmentCoinNewBinding
import com.example.randomgenerator.viewmodel.CoinViewModel
import kotlin.math.roundToInt

class CoinFragment : Fragment() {

    private lateinit var binding: FragmentCoinNewBinding
    private lateinit var viewmodel: CoinViewModel
    private lateinit var upDownAnimator: ValueAnimator
    private lateinit var rotateAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(CoinViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_coin_new,
                container,
                false)
        binding.viewmodel = viewmodel
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        arguments?.takeIf { it.containsKey("ARG_OBJECT") }?.apply {
//            val textView: TextView = view.findViewById(R.id.tv_title)
//            textView.text = getInt("ARG_OBJECT").toString()
//        }
        observeFlipCoin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun startFlipCoinAnimation() {
        binding.ivRandomDevice.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        upDownAnimator.start()
        rotateAnimator.start()
    }

    /**
     * First resource should be tails and second resource is head.
     */
    private fun initRotateAnimator(resourceId: Int, resourceId2: Int) {
        val icon = binding.ivRandomDevice

        val flip = 180
        val numOfFlip = 6f
        val rotations = flip * numOfFlip
        rotateAnimator = ValueAnimator.ofFloat(0f, rotations).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1500
            addUpdateListener {
                val v = it.animatedValue as Float
                icon.rotationX = v

                // Used 80, 260 because the ValueAnimator returns between 85-95 not always 90
                val head = ((v / 10).roundToInt() * 10 - 90) % 360
                val tail = ((v / 10).roundToInt() * 10 - 270) % 360
                if (head == 0) {
                    icon.scaleX = -1f
                    icon.setImageResource(resourceId)
                } else if (tail == 0) {
                    icon.scaleX = 1f
                    icon.setImageResource(resourceId2)
                }
            }
        }
    }

    private fun initUpDownAnimator(resourceId: Int, containerHeight: Int) {
        val icon = binding.ivRandomDevice
        val startY = icon.y
        val marginFromBottom = resources.getDimension(R.dimen.random_device_margin_hao) ?: 800f
        val marginFromTop = (icon.height - marginFromBottom) + marginFromBottom
        val heightOfParentView = binding.clParent.height - marginFromTop
        upDownAnimator = ValueAnimator.ofFloat(startY, heightOfParentView - startY).apply {
            interpolator = DecelerateInterpolator()
            duration = 500
            addUpdateListener { upValueAnimator ->
                val v = upValueAnimator.animatedValue as Float
                icon.y = v

                doOnEnd {
                    val topY = icon.y
                    val flipDown = ValueAnimator.ofFloat(topY, startY).apply {
                        interpolator = AccelerateInterpolator()
                        duration = 500
                        addUpdateListener { downValueAnimator ->
                            val v2 = downValueAnimator.animatedValue as Float
                            icon.y = v2
                        }
                        doOnEnd {
                            icon.scaleX = 1f
                            icon.setImageResource(resourceId)
                        }
                    }
                    flipDown.startDelay = 350
                    flipDown.start()
                }
            }
        }
    }

    private fun observeFlipCoin() {
        viewmodel.flipCoin.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { isHead ->
                val resourceId = if (isHead) R.drawable.ic_random_head_coin else R.drawable.ic_random_tail_coin
                initUpDownAnimator(resourceId, binding.clParent.height)
                initRotateAnimator(R.drawable.ic_random_tail_coin, R.drawable.ic_random_head_coin)
                startFlipCoinAnimation()
            } ?: binding.ivRandomDevice.setImageResource(getCoinResourceId(event.peekContent()))
        })
    }

    private fun getCoinResourceId(isHead: Boolean): Int {
        return if (isHead) R.drawable.ic_random_head_coin else R.drawable.ic_random_tail_coin
    }
}