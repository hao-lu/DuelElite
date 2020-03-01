package com.lucidity.haolu.lifepointcalculator.view.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentCalculatorBinding
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.Player
import com.lucidity.haolu.lifepointcalculator.util.Constants
import com.lucidity.haolu.lifepointcalculator.viewmodel.CalculatorViewModel
import kotlin.math.abs

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var viewmodel: CalculatorViewModel

    private val ANIMATION_DURATION: Long = 500

    companion object {
        fun newInstance() =
            CalculatorFragment()
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
        binding.ibHistory.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.BUNDLE_KEY_LIFE_POINT_LOG, viewmodel.log)
            findNavController(this).navigate(
                R.id.action_fragment_calculator_to_fragment_log,
                bundle
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActionLp()
        observeActionHint()
        observeDuelTime()
        observePlayerOneLp()
        observePlayerTwoLp()
    }

    override fun onResume() {
        super.onResume()
        viewmodel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewmodel.onPause()
    }

    private fun observeActionLp() {
        viewmodel.actionLp.observe(viewLifecycleOwner, Observer { lp ->
            binding.ivPlayerOneLastLpIndicator.visibility = View.INVISIBLE
            binding.ivPlayerTwoLastLpIndicator.visibility = View.INVISIBLE
            if (viewmodel.animate && lp.first != lp.second) {
                animateLpValue(lp.first, lp.second, binding.tvActionLp, true)
            } else if (viewmodel.halve) {
                binding.tvActionLp.text = resources.getText(R.string.halve)
            } else if (lp.second == 0) { // Clear
                binding.tvActionLp.text = resources.getText(R.string.empty)
                viewmodel.log.getLatestEntry()?.let { logItem ->
                    val indicatorIcon =
                        if (logItem.player == Player.ONE.name) binding.ivPlayerOneLastLpIndicator else binding.ivPlayerTwoLastLpIndicator
                    indicatorIcon.visibility = View.VISIBLE
                }
            } else {
                binding.tvActionLp.text = lp.second.toString()
            }
        })
    }

    private fun observeActionHint() {
        viewmodel.actionLpHint.observe(viewLifecycleOwner, Observer { logItem ->
            binding.ivPlayerOneLastLpIndicator.visibility = View.INVISIBLE
            binding.ivPlayerTwoLastLpIndicator.visibility = View.INVISIBLE
            val drawableId =
                if (logItem.actionLp < 0) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up
            val indicatorIcon =
                if (logItem.player == Player.ONE.name) binding.ivPlayerOneLastLpIndicator else binding.ivPlayerTwoLastLpIndicator
            indicatorIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), drawableId))
            indicatorIcon.visibility = View.VISIBLE
            binding.tvActionLp.hint = abs(logItem.actionLp).toString()
        })
    }

    private fun observePlayerOneLp() {
        viewmodel.playerOneLp.observe(viewLifecycleOwner, Observer { lp ->
            animatePlayerLp(
                resources.getString(R.string.snack_bar_message_player_two_won),
                lp,
                binding.tvPlayerOneLp,
                binding.vBarPlayerOneLp,
                binding.vBarPlayerOneLpBackground
            )
        })
    }

    private fun observePlayerTwoLp() {
        viewmodel.playerTwoLp.observe(viewLifecycleOwner, Observer { lp ->
            animatePlayerLp(
                resources.getString(R.string.snack_bar_message_player_one_won),
                lp,
                binding.tvPlayerTwoLp,
                binding.vBarPlayerTwoLp,
                binding.vBarPlayerTwoLpBackground
            )
        })
    }

    private fun observeDuelTime() {
        viewmodel.timer.duelTime.observe(viewLifecycleOwner, Observer { duelTime ->
            if (viewmodel.timer.isStarted) {
                binding.ibDuelTime.visibility = View.INVISIBLE
                binding.tvDuelTime.visibility = View.VISIBLE
                binding.tvDuelTime.text = duelTime
            }
        })
    }

    private fun calculateLpBarWidth(lp: Double, width: Int) =
        (lp / LifePointCalculator.START_LP * width).toInt()

    private fun animatePlayerLp(
        message: String,
        lp: Pair<Int, Int>,
        lpTextView: TextView,
        lpBar: View,
        lpBarBackground: View
    ) {
        if (lp.second == 0) {
            showResetSnackbar(message)
        }
        if (viewmodel.animate) {
            animateLpValue(lp.first, lp.second, lpTextView, false)
            animateLpBar(lp.first, lp.second, lpBar, lpBarBackground.width)
        } else {
            lpTextView.text = lp.second.toString()
            binding.root.doOnLayout {
                lpBar.layoutParams.width = calculateLpBarWidth(lp.second.toDouble(), lpBarBackground.width)
                lpBar.requestLayout()
            }
        }
    }

    private fun animateLpValue(currLp: Int, newLp: Int, text: TextView, clearText: Boolean) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener {
                text.text = it.animatedValue.toString()
            }
            this.addUpdateListener { value ->
                doOnEnd {
                    if (value.animatedValue.toString().toInt() == 0 && clearText) {
                        text.text = resources.getText(R.string.empty)
                    }
                }
            }
        }.start()
    }

    private fun animateLpBar(currLp: Int, newLp: Int, view: View, width: Int) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener { valueAnimator ->
                // Animate the bar, use double or there's truncation with int
                val animatedValue = valueAnimator.animatedValue.toString()
                val ans = calculateLpBarWidth(animatedValue.toDouble(), width)
                when {
                    ans == 0 -> view.visibility = View.GONE
                    ans >= width -> view.layoutParams.width = width
                    else -> view.layoutParams.width = ans
                }
                view.requestLayout()
            }
        }.start()
    }

    private fun showResetSnackbar(text: String) {
        val snackBar = Snackbar.make(binding.root, text, 5000)
            .setAction("RESET", { reset() })
            .setActionTextColor(Color.WHITE)
        snackBar.config(requireContext())
        snackBar.show()
    }

    private fun reset() {
        viewmodel.reset()
        binding.tvActionLp.hint = resources.getString(R.string.action_lp_hint)
        binding.ivPlayerOneLastLpIndicator.visibility = View.INVISIBLE
        binding.ivPlayerTwoLastLpIndicator.visibility = View.INVISIBLE
        binding.vBarPlayerOneLp.visibility = View.VISIBLE
        binding.vBarPlayerTwoLp.visibility = View.VISIBLE
        binding.ibDuelTime.visibility = View.VISIBLE
        binding.tvDuelTime.visibility = View.INVISIBLE
    }

    // TODO: shared module
    fun Snackbar.config(context: Context) {
        val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
        val margin = context.resources.getDimension(R.dimen.snackbar_margins).toInt()
        params.setMargins(margin, margin, margin, margin)
        this.view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = v.paddingBottom - insets.systemWindowInsetBottom)
            insets
        }
        this.view.layoutParams = params
        this.view.background = context.getDrawable(R.drawable.bg_snackbar)
        ViewCompat.setElevation(this.view, 6f)
    }
}