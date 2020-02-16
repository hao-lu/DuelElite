package com.lucidity.haolu.lifepointcalculator.view

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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
        observeNormalViewModel()
        observeBaseViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun observeNormalViewModel() {
        viewmodel.actionLp.observe(viewLifecycleOwner, Observer { lp ->
            if (lp.first == lp.second) {
                binding.tvActionLp.text = resources.getText(R.string.empty)
            } else {
                animateValue(lp.first, lp.second, binding.tvActionLp, true)
            }
        })

        viewmodel.halve.observe(viewLifecycleOwner, Observer { stringId ->
            binding.tvActionLp.text = resources.getText(stringId)
        })
    }

    // TODO: clean up
    private fun observeBaseViewModel() {
        viewmodel.playerOneLp.observe(viewLifecycleOwner, Observer { lp ->
            animateValue(lp.first, lp.second, binding.tvPlayerOneLp, false)
            animateLpBar(
                lp.first,
                lp.second,
                binding.vBarPlayerOneLp,
                binding.vBarPlayerOneLpBackground.width
            )
            binding.ivPlayerTwoLastLpIndicator.visibility = View.INVISIBLE
            binding.ivPlayerOneLastLpIndicator.setImageDrawable(ContextCompat.getDrawable(requireContext(), lp.third))
            binding.ivPlayerOneLastLpIndicator.visibility = View.VISIBLE
        })

        viewmodel.playerTwoLp.observe(viewLifecycleOwner, Observer { lp ->
            animateValue(lp.first, lp.second, binding.tvPlayerTwoLp, false)
            animateLpBar(
                lp.first,
                lp.second,
                binding.vBarPlayerTwoLp,
                binding.vBarPlayerOneLpBackground.width
            )
            binding.ivPlayerOneLastLpIndicator.visibility = View.INVISIBLE
            binding.ivPlayerTwoLastLpIndicator.setImageDrawable(ContextCompat.getDrawable(requireContext(), lp.third))
            binding.ivPlayerTwoLastLpIndicator.visibility = View.VISIBLE
        })

        viewmodel.timer.duelTime.observe(viewLifecycleOwner, Observer {
            binding.ibDuelTime.visibility = View.INVISIBLE
            binding.tvDuelTime.visibility = View.VISIBLE
            binding.tvDuelTime.text = it
        })
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView, clearText: Boolean) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener {
                text.text = it.animatedValue.toString()
            }
            this.addUpdateListener { value ->
                doOnEnd {
                    if (value.animatedValue.toString().toInt() == 0 && clearText) text.text = resources.getText(R.string.empty)
                }
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