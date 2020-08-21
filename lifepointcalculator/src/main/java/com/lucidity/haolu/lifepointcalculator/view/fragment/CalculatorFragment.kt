package com.lucidity.haolu.lifepointcalculator.view.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lucidity.haolu.lifepointcalculator.BR
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentCalculatorBinding
import com.lucidity.haolu.lifepointcalculator.model.LifePointCalculator
import com.lucidity.haolu.lifepointcalculator.model.Player
import com.lucidity.haolu.lifepointcalculator.util.Constants
import com.lucidity.haolu.lifepointcalculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var viewmodel: CalculatorViewModel

    private var sharedPreferences: SharedPreferences? = null

    private val ANIMATION_DURATION: Long = 500
    private val SNACKBAR_DURATION: Int = 10000

    companion object {
        const val TAG = "CalculatorFragment"

        fun newInstance() = CalculatorFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
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
        inflateInputLayout(R.layout.layout_normal_input, binding.vsNormalInput.viewStub)
        inflateInputLayout(R.layout.layout_accumulated_input, binding.vsAccumulatedInput.viewStub)
        binding.vsAccumulatedInput.viewStub?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActionLp()
        observeActionHint()
        observeAnimateActionLp()
        observeDuelTime()
        observePlayerOneLp()
        observeAnimatePlayerOneLp()
        observePlayerTwoLp()
        observeAnimatePlayerTwoLp()
        observePlayerOneLastLpIndicatorInvisible()
        observePlayerTwoLastLpIndicatorInvisible()
        observeLastLpIndicatorDrawable()
        observeShowResetSnackbar()
        observeShowInputTypeBottomSheet()
        observeShowNormalInput()
        observeShowAccumulatedInput()
        observeNavigateLogEvent()
    }

    private fun inflateInputLayout(layoutId: Int, viewStub: ViewStub?) {
        viewStub?.layoutResource = layoutId
        viewStub?.setOnInflateListener { stub, inflated ->
            val binding = DataBindingUtil.bind<ViewDataBinding>(inflated)
            binding?.setVariable(BR.viewmodel, viewmodel)
        }
        viewStub?.inflate()
    }

    private fun observeAnimateActionLp() {
        viewmodel.animateActionLp.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { lp ->
                animateLpValue(lp.first, lp.second, binding.tvActionLp, true)
            }
        })
    }

    private fun observeShowResetSnackbar() {
        viewmodel.showResetSnackbar.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { text ->
                showResetSnackbar(text)
            }
        })
    }

    private fun observePlayerOneLastLpIndicatorInvisible() {
        viewmodel.playerOneLpIndicatorInvisibility.observe(
            viewLifecycleOwner,
            Observer { isInvisible ->
                binding.ivPlayerOneLastLpIndicator.isInvisible = isInvisible
            })
    }

    private fun observePlayerTwoLastLpIndicatorInvisible() {
        viewmodel.playerTwoLpIndicatorInvisibility.observe(
            viewLifecycleOwner,
            Observer { isInvisible ->
                binding.ivPlayerTwoLastLpIndicator.isInvisible = isInvisible
            })
    }

    private fun observeLastLpIndicatorDrawable() {
        viewmodel.lpIndicatorDrawableId.observe(viewLifecycleOwner, Observer { drawableId ->
            val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
            binding.ivPlayerOneLastLpIndicator.setImageDrawable(drawable)
            binding.ivPlayerTwoLastLpIndicator.setImageDrawable(drawable)
        })
    }

    private fun observeActionLp() {
        viewmodel.actionLp.observe(viewLifecycleOwner, Observer { lp ->
            binding.tvActionLp.text = lp?.toString() ?: ""
        })
    }

    private fun observeActionHint() {
        viewmodel.actionLpHint.observe(viewLifecycleOwner, Observer { hint ->
            binding.tvActionLp.hint = hint
        })
    }

    private fun observePlayerOneLp() {
        viewmodel.playerOneLp.observe(viewLifecycleOwner, Observer { lp ->
            binding.tvPlayerOneLp.text = lp.toString()
            binding.root.doOnLayout {
                binding.vBarPlayerOneLp.layoutParams.width =
                    calculateLpBarWidth(lp.toDouble(), binding.vBarPlayerLpBackground.width / 2)
                binding.vBarPlayerOneLp.isInvisible = viewmodel.playerOneLpBarInvisible
                binding.vBarPlayerOneLp.requestLayout()
            }
        })
    }

    private fun observePlayerTwoLp() {
        viewmodel.playerTwoLp.observe(viewLifecycleOwner, Observer { lp ->
            binding.tvPlayerTwoLp.text = lp.toString()
            binding.root.doOnLayout {
                binding.vBarPlayerTwoLp.layoutParams.width =
                    calculateLpBarWidth(lp.toDouble(), binding.vBarPlayerLpBackground.width / 2)
                binding.vBarPlayerTwoLp.isInvisible = viewmodel.playerTwoLpBarInvisible
                binding.vBarPlayerTwoLp.requestLayout()
            }
        })
    }

    private fun observeAnimatePlayerOneLp() {
        viewmodel.animatePlayerOneLp.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { lp ->
                animateLpValue(lp.first, lp.second, binding.tvPlayerOneLp, false)
                animateLpBar(
                    Player.ONE,
                    lp.first,
                    lp.second,
                    binding.vBarPlayerOneLp,
                    binding.vBarPlayerLpBackground.width / 2
                )
            }
        })
    }

    private fun observeAnimatePlayerTwoLp() {
        viewmodel.animatePlayerTwoLp.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { lp ->
                animateLpValue(lp.first, lp.second, binding.tvPlayerTwoLp, false)
                animateLpBar(
                    Player.TWO,
                    lp.first,
                    lp.second,
                    binding.vBarPlayerTwoLp,
                    binding.vBarPlayerLpBackground.width / 2
                )
            }
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

    private fun observeShowInputTypeBottomSheet() {
        viewmodel.showInputTypeBottomSheet.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                InputTypeBottomSheetDialogFragment().show(childFragmentManager, InputTypeBottomSheetDialogFragment.TAG)
            }
        })
    }

    private fun observeShowAccumulatedInput() {
        viewmodel.showAccumulatedInput.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                binding.vsAccumulatedInput.viewStub?.visibility = View.VISIBLE
                binding.vsNormalInput.viewStub?.visibility = View.GONE
            }
        })
    }

    private fun observeShowNormalInput() {
        viewmodel.showNormalInput.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                binding.vsAccumulatedInput.viewStub?.visibility = View.GONE
                binding.vsNormalInput.viewStub?.visibility = View.VISIBLE
            }
        })
    }

    private fun observeNavigateLogEvent() {
        viewmodel.navigateToLogEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                val bundle = Bundle()
                bundle.putParcelable(Constants.BUNDLE_KEY_LIFE_POINT_LOG, viewmodel.log)
                findNavController(this).navigate(
                    R.id.action_fragment_calculator_to_fragment_log,
                    bundle
                )
            }
        })
    }

    private fun calculateLpBarWidth(lp: Double, width: Int) =
        (lp / LifePointCalculator.START_LP * width).toInt()

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

    private fun animateLpBar(player: Player, currLp: Int, newLp: Int, view: View, width: Int) {
        ValueAnimator.ofInt(currLp, newLp).apply {
            this.duration = ANIMATION_DURATION
            this.addUpdateListener { valueAnimator ->
                // Animate the bar, use double or there's truncation with int
                val animatedValue = valueAnimator.animatedValue.toString().toDouble()
                val ans = calculateLpBarWidth(animatedValue, width)
                when {
                    ans == 0 -> {
                        view.visibility = View.GONE
                        if (player == Player.ONE) viewmodel.playerOneLpBarInvisible = true
                        else viewmodel.playerTwoLpBarInvisible = true
                    }
                    ans >= width -> view.layoutParams.width = width
                    else -> view.layoutParams.width = ans
                }
                view.requestLayout()
            }
        }.start()
    }

    private fun showResetSnackbar(text: String) {
        val snackBar = Snackbar.make(binding.root, text, SNACKBAR_DURATION)
            .setAction(R.string.snackbar_action_reset, { reset() })
            .setActionTextColor(Color.WHITE)
        snackBar.config(requireContext(), binding.root.width)
        snackBar.show()
    }

    private fun reset() {
        viewmodel.reset()
        binding.ibDuelTime.visibility = View.VISIBLE
        binding.tvDuelTime.visibility = View.INVISIBLE
    }

    // TODO: shared module
    fun Snackbar.config(context: Context, width: Int) {
        val params = this.view.layoutParams as CoordinatorLayout.LayoutParams
        val margin = context.resources.getDimension(R.dimen.snackbar_margins).toInt()
//        this.view.setOnApplyWindowInsetsListener { v, insets ->
//            v.updatePadding(bottom = v.paddingBottom - insets.systemWindowInsetBottom)
//            insets
//        }
        params.width = width - (margin * 2)
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        this.view.layoutParams = params
        this.view.background = context.getDrawable(R.drawable.bg_snackbar)
        ViewCompat.setElevation(this.view, 6f)
    }
}
