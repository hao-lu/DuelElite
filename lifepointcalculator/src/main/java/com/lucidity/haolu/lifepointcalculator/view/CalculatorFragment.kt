package com.lucidity.haolu.lifepointcalculator.view

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
import com.lucidity.haolu.lifepointcalculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var viewmodel: CalculatorViewModel

    private val ANIMATION_DURATION: Long = 500

    companion object {
        fun newInstance() = CalculatorFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCalculatorBinding>(
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
        observeCumulatedLp()
    }

    private fun observeCumulatedLp() {
        viewmodel.cumulatedLp.observe(viewLifecycleOwner, Observer {
            if (it == -1) {
                binding.etCumulatedLp.setText(R.string.halve)
            } else {
                animateValue(viewmodel.previousCumulatedLp, it, binding.etCumulatedLp)
            }
        })

        viewmodel.playerOneLp.observe(viewLifecycleOwner, Observer {
            animateValue(viewmodel.previousPlayerLp, it, binding.tvPlayerOneLp)
            animateLpBar(
                viewmodel.previousPlayerLp,
                it,
                binding.vBarPlayerOneLp,
                binding.vBarPlayerOneLpBackground.width
            )
        })

        viewmodel.playerTwoLp.observe(viewLifecycleOwner, Observer {
            animateValue(viewmodel.previousPlayerLp, it, binding.tvPlayerTwoLp)
            animateLpBar(
                viewmodel.previousPlayerLp,
                it,
                binding.vBarPlayerTwoLp,
                binding.vBarPlayerOneLpBackground.width
            )
        })
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener {
                text.text = it.animatedValue.toString()
            }
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