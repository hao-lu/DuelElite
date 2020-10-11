package com.lucidity.haolu.randomgenerator.view.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lucidity.haolu.randomgenerator.R
import com.lucidity.haolu.randomgenerator.databinding.FragmentDiceNewBinding
import com.lucidity.haolu.randomgenerator.model.RandomGenerator
import com.lucidity.haolu.randomgenerator.viewmodel.DiceViewModel
import kotlin.math.roundToInt

// TODO: clean up and combine with coin
class DiceFragment : Fragment() {

    private lateinit var binding: FragmentDiceNewBinding
    private lateinit var viewmodel: DiceViewModel
    private lateinit var upDownAnimator: ValueAnimator
    private lateinit var rotateAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(DiceViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_dice_new,
                container,
                false)
        binding.viewmodel = viewmodel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey("ARG_OBJECT") }?.apply {
//            val textView: TextView = view.findViewById(R.id.tv_title)
//            textView.text = getInt("ARG_OBJECT").toString()
//        }
        observeRollDice()
    }

    private fun observeRollDice() {
        viewmodel.rollDice.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { rolledNumber ->
                initUpDownAnimator(rolledNumber)
                initRotateAnimator()
                startRollDiceAnimation()
            } ?: binding.diceHao.setImageResource(getDiceResourceId(event.peekContent()))
        })
    }

    private fun startRollDiceAnimation() {
        binding.diceHao.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        binding.diceHao.isClickable = false
        upDownAnimator.start()
        rotateAnimator.start()
    }

    private fun initUpDownAnimator(diceResourceId: Int) {
        val icon = binding.diceHao
        val startY = icon.y
        val marginFromBottom = resources.getDimension(R.dimen.random_device_margin_hao) ?: 800f
        val marginFromTop = (icon.height - marginFromBottom) + marginFromBottom
        val heightOfParentView = binding.otherParent.height - marginFromTop
        upDownAnimator = ValueAnimator.ofFloat(startY, heightOfParentView - startY).apply {
            interpolator = AccelerateDecelerateInterpolator()
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
                            icon.setImageResource(getDiceResourceId(diceResourceId))
                            icon.isClickable = true
                        }
                    }
                    flipDown.startDelay = 300
                    flipDown.start()
                }
            }
        }
    }

    private fun initRotateAnimator() {
        val icon = binding.diceHao

        val flip = 180
        val numOfFlip = 6f
        val rotations = flip * numOfFlip
        rotateAnimator = ValueAnimator.ofFloat(0f, 720f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1300
            addUpdateListener {
                val v = it.animatedValue as Float
                icon.rotation = v

                // + 10 don't start changing until it going up and < 600 don't change anymore going down
                if (((v / 10).roundToInt() * 10 + 10) % 90 == 0 && v < 540) {
                    icon.setImageResource(getDiceResourceId(RandomGenerator.roll()))
                }
            }
        }
    }

    private fun getDiceResourceId(value: Int): Int {
        return when (value) {
            0 -> R.drawable.ic_random_dice_1
            1 -> R.drawable.ic_random_dice_2
            2 -> R.drawable.ic_random_dice_3
            3 -> R.drawable.ic_random_dice_4
            4 -> R.drawable.ic_random_dice_5
            else -> R.drawable.ic_random_dice_6
        }
    }
}
