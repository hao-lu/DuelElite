package com.lucidity.haolu.lifepointcalculator.view

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentCalculatorBinding
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.viewmodel.CumulatedCalculatorViewModel
import com.lucidity.haolu.lifepointcalculator.viewmodel.NormalCalculatorViewModel

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
//    private lateinit var viewmodel: CumulatedCalculatorViewModel
    private lateinit var viewmodel: NormalCalculatorViewModel

    private val ANIMATION_DURATION: Long = 500

    companion object {
        fun newInstance() = CalculatorFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewmodel = ViewModelProvider(this).get(CumulatedCalculatorViewModel::class.java)
        viewmodel = ViewModelProvider(this).get(NormalCalculatorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_calculator,
            container,
            false
        )
        binding.viewmodel = viewmodel
        binding.lInput.viewmodel = viewmodel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        observeCumulatedViewModel()
        observeNormalViewModel()
        observeBaseViewModel()
    }

    private fun observeNormalViewModel() {
        viewmodel.actionLp.observe(viewLifecycleOwner, Observer {
                binding.tvActionLp.text = it.toString()
        })
    }

//    private fun observeCumulatedViewModel() {
//        viewmodel.actionLp.observe(viewLifecycleOwner, Observer {
//            animateValue(viewmodel.previousActionLp, it, binding.tvActionLp)
//        })
//
//        viewmodel.isHalve.observe(viewLifecycleOwner, Observer {
//            binding.tvActionLp.setText(R.string.halve)
//        })
//    }

    // TODO: clean up
    private fun observeBaseViewModel() {
        viewmodel.playerOneLp.observe(viewLifecycleOwner, Observer { currentPlayerLp ->
            animateValue(viewmodel.previousPlayerLp, currentPlayerLp, binding.tvPlayerOneLp)
            animateLpBar(
                viewmodel.previousPlayerLp,
                currentPlayerLp,
                binding.vBarPlayerOneLp,
                binding.vBarPlayerOneLpBackground.width
            )
            animateValue(viewmodel.previousActionLp, 0, binding.tvActionLp)
        })

        viewmodel.playerTwoLp.observe(viewLifecycleOwner, Observer { currentPlayerLp ->
            animateValue(viewmodel.previousPlayerLp, currentPlayerLp, binding.tvPlayerTwoLp)
            animateLpBar(
                viewmodel.previousPlayerLp,
                currentPlayerLp,
                binding.vBarPlayerTwoLp,
                binding.vBarPlayerOneLpBackground.width
            )
            animateValue(viewmodel.previousActionLp, 0, binding.tvActionLp)
        })

        viewmodel.timer.duelTime.observe(viewLifecycleOwner, Observer {
            binding.ibDuelTime.visibility = View.INVISIBLE
            binding.tvDuelTime.visibility = View.VISIBLE
            binding.tvDuelTime.text = it
        })
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener {
                text.text = it.animatedValue.toString()
            }
//            this.addListener {
//                doOnEnd {  }
//            }
//            this.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator) {
//
//                }
//            })
        }.start()

    }

    private fun animateLpBar(currLp: Int, newLp: Int, view: View, width: Int) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener {
                // Animate the bar, use double or there's truncation with int
                val ans = (it.animatedValue.toString().toDouble() / LifePointCalculator.START_LP * width)
                when {
                    ans.toInt() == 0 -> view.visibility = View.GONE
                    ans >= width -> view.layoutParams.width = width
                    else -> view.layoutParams.width = ans.toInt()
                }
                view.requestLayout()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}