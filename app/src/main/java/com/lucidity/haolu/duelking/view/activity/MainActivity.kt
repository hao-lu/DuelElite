package com.lucidity.haolu.duelking.view.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.lucidity.haolu.duelking.BR
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.databinding.ActivityMainBinding
import com.lucidity.haolu.duelking.model.LifePointCalculator
import com.lucidity.haolu.duelking.view.fragment.CustomLpFragment
import kotlinx.android.synthetic.main.content_main.*
import io.realm.Realm
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), CustomLpFragment.CustomLpDialogListener {

    private val TAG = "MainActivity"

    /**
     * Allows the CountDownTimer to be Pauseable.
     *
     * @param millisInFuture The time remaining
     * @param countDownInterval The count down interval, e.g., 1000ms is 1s
     */
    private inner class CountDownTimerPauseable(val millisInFuture: Long, val countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            val formatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            )
            text_duel_time.text = formatted
            mTimeRemaining = millisUntilFinished
        }

        override fun onFinish() {
            text_duel_time.text = resources.getString(R.string.round_over)
        }
    }

    private val START_LP = "8000"
    private val DUEL_TIME_TEXT = "40:00"
    private val DUEL_TIME = 2400000L

    private var mTimeRemaining = DUEL_TIME
    private var mTimer = CountDownTimerPauseable(mTimeRemaining, 1000)
    private var mIsTimerRunnning = false

    private var mLpCalculator = LifePointCalculator()
    private var mRealm = Realm.getDefaultInstance()
    private var mValueAnimator = ValueAnimator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        binding.contentMain.setVariable(BR.LPCalculator, mLpCalculator)
        binding.contentMain.executePendingBindings()

        // Allows the user to user number buttons to add onto custom input
        editText_cumulated_lp.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mLpCalculator.mCumulatedLp = editText_cumulated_lp.text.toString().toInt()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText_cumulated_lp.windowToken, 0)
                editText_cumulated_lp.clearFocus()
                return@setOnEditorActionListener true
            }
            // Return false to dismiss keyboard
            return@setOnEditorActionListener false
        }
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) fragmentManager.popBackStack()
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            val intent = Intent(this, SearchableCardActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.action_reset)
            reset()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    // CustomLpDialogListener interface functions
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val currLp = mLpCalculator.mCumulatedLp
        val editText = dialog.dialog?.findViewById(R.id.edit_custom) as EditText
        // Checking for integer overflow

        try {
            mLpCalculator.mCumulatedLp += editText.text.toString().toInt()
        } catch (nfe: NumberFormatException) {
        }
        val newLp = mLpCalculator.mCumulatedLp
        animateValue(currLp, newLp, editText_cumulated_lp)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // Cancel
    }

    private fun reset() {
        mLpCalculator.reset()
        text_player_one_lp.text = START_LP
        text_player_two_lp.text = START_LP
        text_duel_time.text = DUEL_TIME_TEXT
        editText_cumulated_lp.setText("", TextView.BufferType.EDITABLE)
        mTimer.cancel()
        mTimeRemaining = DUEL_TIME
        mIsTimerRunnning = false
        text_duel_time.visibility = TextView.INVISIBLE
        imageBtn_duel_time.visibility = ImageButton.VISIBLE

        val ll = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        bar_player_one.layoutParams = ll
        bar_player_two.layoutParams = ll
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView): ValueAnimator {
        val animator = ValueAnimator.ofInt(currLp, newLp)
        animator.duration = 500
        animator.addUpdateListener {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            text.text = it.animatedValue.toString()
            // Show hint
            if (it.animatedValue.toString() == "0") editText_cumulated_lp.setText("", TextView.BufferType.EDITABLE)
            // Animate the bar, use integer so there's truncation
//            if (it.animatedValue.toString().toInt() in 0..8025) {
            if (it.animatedValue.toString().toInt() <= 8000) {
                if (text.id == R.id.text_player_one_lp) {
                    val ans = it.animatedValue.toString().toDouble() / 8000 * width
                    val ll = FrameLayout.LayoutParams(ans.toInt(), FrameLayout.LayoutParams.MATCH_PARENT)
                    bar_player_one.layoutParams = ll
//                    LifePointLog.d(TAG, it.animatedValue.toString())
                } else if (text.id == R.id.text_player_two_lp) {
                    val ans = it.animatedValue.toString().toDouble() / 8000 * width
                    val ll = FrameLayout.LayoutParams(ans.toInt(), FrameLayout.LayoutParams.MATCH_PARENT)
                    ll.gravity = Gravity.END
                    bar_player_two.layoutParams = ll
                }
            }
            else {
                val ll = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                if (text.id == R.id.text_player_one_lp) bar_player_one.layoutParams = ll
                else if (text.id == R.id.text_player_two_lp) bar_player_two.layoutParams = ll
            }
        }
        animator.start()
        return animator
    }

    fun timerClicked(view: View) {
        if (view.id == R.id.imageBtn_duel_time) {
            view.visibility = ImageButton.INVISIBLE
            text_duel_time.visibility = TextView.VISIBLE
        }
        mTimer.cancel()
        if (!mIsTimerRunnning) {
            mTimer = CountDownTimerPauseable(mTimeRemaining, 1000)
            mTimer.start()
            mIsTimerRunnning = true
            Snackbar.make(view, "Timer started", Snackbar.LENGTH_SHORT).show()
        } else {
            mIsTimerRunnning = false
            Snackbar.make(view, "Timer paused", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun numberButtonClicked(view: View) {
        val currLp = mLpCalculator.mCumulatedLp
        // Update the mCumulatedLp
        if (view is TextView) mLpCalculator.mCumulatedLp += view.text.toString().toInt()
        val newLp = mLpCalculator.mCumulatedLp
        mValueAnimator = animateValue(currLp, newLp, editText_cumulated_lp)
    }

    // Update the mPlayer life points (gain/lose)
    fun playerButtonClicked(view: View) {
        val currLp: Int
        val newLp: Int
        val currPlayer: TextView
        var cumulateLp = mLpCalculator.mCumulatedLp
        var add = false // add button or subtract
        val editText = editText_cumulated_lp.text.toString()
        var halve = false
        if (editText == "HALVE") halve = true

        // Custom input in EditText
        if (!mValueAnimator.isRunning && editText != "HALVE" && editText != "") {
            mLpCalculator.mCumulatedLp = editText.toInt()
            cumulateLp = editText.toInt()
        }

        // Check which button was clicked + or -
        if (view.tag == "plus") add = true
        val parentView = view.parent as View

        if (parentView.id == R.id.player_one_buttons) {
            currLp = mLpCalculator.mPlayerOneLp
            currPlayer = text_player_one_lp
            mLpCalculator.updateLP(add, true, halve)
            newLp = mLpCalculator.mPlayerOneLp
        } else {
            currLp = mLpCalculator.mPlayerTwoLp
            currPlayer = text_player_two_lp
            mLpCalculator.updateLP(add, false, halve)
            newLp = mLpCalculator.mPlayerTwoLp
        }

        // Animate currPlayer losing life points
        animateValue(currLp, newLp, currPlayer)
        // Animate decreasing of cumulated lp
        animateValue(cumulateLp, 0, editText_cumulated_lp)

        if (mLpCalculator.mPlayerOneLp == 0 && mLpCalculator.mPlayerTwoLp != 0) {
            val snackBar = Snackbar.make(view, "Player 2 has won", Snackbar.LENGTH_LONG)
                    .setAction("RESET", { reset() })
                    .setActionTextColor(Color.WHITE)
            snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYugiBlue))
            snackBar.show()
        } else if (mLpCalculator.mPlayerTwoLp == 0 && mLpCalculator.mPlayerOneLp != 0) {
            val snackBar = Snackbar.make(view, "Player 1 has won", Snackbar.LENGTH_LONG)
                    .setAction("RESET", { reset() })
                    .setActionTextColor(Color.WHITE)
            snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYugiBlue))
            snackBar.show()
        }

        // Remove focus from EditText
        cardView_utility.requestFocus()
    }

    // Halve the life points
    fun halfButtonClicked(view: View) {
        editText_cumulated_lp.setText("HALVE", TextView.BufferType.EDITABLE)
    }

    fun clearButtonClicked(view: View) {
        editText_cumulated_lp.setText("", TextView.BufferType.EDITABLE)
        mLpCalculator.mCumulatedLp = 0
    }

    // Launch mLog activity
    fun logButtonClicked(view: View) {
        val intent = Intent(view.context, LogActivity::class.java)
        intent.putExtra("mLog", mLpCalculator.mLog)
        startActivity(intent)
    }

    fun rngButtonClicked(view: View) {
        val intent = Intent(this, RngActivity::class.java)
        startActivity(intent)
    }
}
