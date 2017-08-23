package com.lucidity.haolu.duelking

import android.animation.ValueAnimator
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import com.lucidity.haolu.duelking.databinding.AppBarMainBinding
import kotlinx.android.synthetic.main.content_main.*
import io.realm.Realm
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CustomLpFragment.CustomLpDialogListener {

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
            text_duel_time.text = "Round Over"
        }
    }

    private val START_LP = "8000"
    private val DUEL_TIME_TEXT = "40:00"
    private val DUEL_TIME = 2400000L

    private var mTimeRemaining = 2400000L
    private var mTimer = CountDownTimerPauseable(mTimeRemaining, 1000)
    private var mIsTimerRunnning = false

    private var mLpCalculator = LifePointCalculator()
    private var mRealm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: AppBarMainBinding = DataBindingUtil.setContentView(this, R.layout.app_bar_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val intent = Intent(this, SearchCardActivity::class.java)
            startActivity(intent)
        }
//
//        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
//        val toggle = ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer.setDrawerListener(toggle)
//        toggle.syncState()

//        val navigationView = findViewById(R.id.nav_view) as NavigationView
//        navigationView.setNavigationItemSelectedListener(this)
        binding.contentMain.setVariable(BR.LPCalculator, mLpCalculator)
        binding.contentMain.executePendingBindings()
    }

    override fun onBackPressed() {
//        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }

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
        if (id == R.id.action_reset)
            reset()
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_calculator) {
            // Handle the camera action
        } else if (id == R.id.nav_search) {
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    // CustomLpDialogListener interface functions
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val currLp = mLpCalculator.mCumulatedLp
        val editText = dialog.dialog.findViewById(R.id.edit_custom) as EditText
        // Checking for integer overflow

        try {
            mLpCalculator.mCumulatedLp += editText.text.toString().toInt()
        }
        catch (nfe: NumberFormatException) {}
        val newLp = mLpCalculator.mCumulatedLp
        animateValue(currLp, newLp, text_cumulated_lp)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // Cancel
    }

    private fun reset() {
        mLpCalculator.reset()
        text_player_one_lp.text = START_LP
        text_player_two_lp.text = START_LP
        text_duel_time.text = DUEL_TIME_TEXT
        text_cumulated_lp.text = "0"
        mTimer.cancel()
        mTimeRemaining = DUEL_TIME
        mIsTimerRunnning = false
        addOrSubtractToggle.isChecked = false
        addOrSubtractToggle.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorLose))
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView) {
        val animator = ValueAnimator.ofInt(currLp, newLp)
        animator.duration = 500
        animator.addUpdateListener { text.text = it.animatedValue.toString() }
        animator.start()
    }

    fun timerClicked(view: View) {
        mTimer.cancel()
        if (!mIsTimerRunnning) {
            mTimer = CountDownTimerPauseable(mTimeRemaining, 1000)
            mTimer.start()
            mIsTimerRunnning = true
            Snackbar.make(view, "Timer started", Snackbar.LENGTH_SHORT).show()
        }
        else {
            mIsTimerRunnning = false
            Snackbar.make(view, "Timer paused", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun numberButtonClicked(view: View) {
        val currLp = mLpCalculator.mCumulatedLp
        // Update the mCumulatedLp
        if (view is TextView) mLpCalculator.mCumulatedLp += view.text.toString().toInt()
        val newLp = mLpCalculator.mCumulatedLp
        animateValue(currLp, newLp, text_cumulated_lp)
    }

    // Update the mPlayer life points (gain/lose)
    fun playerButtonClicked(view: View) {
        val currLp: Int
        val newLp: Int
        val currPlayer: TextView
        val cumulateLp = mLpCalculator.mCumulatedLp

        if (view.id == R.id.button_player_one) {
            currLp = mLpCalculator.mPlayerOneLp
            currPlayer = text_player_one_lp
            mLpCalculator.updateLP(addOrSubtractToggle.isChecked, true)
            newLp = mLpCalculator.mPlayerOneLp
        }
        else {
            currLp = mLpCalculator.mPlayerTwoLp
            currPlayer = text_player_two_lp
            mLpCalculator.updateLP(addOrSubtractToggle.isChecked, false)
            newLp = mLpCalculator.mPlayerTwoLp
        }

        // Animate mPlayer losing life points
        animateValue(currLp, newLp, currPlayer)
        // Animate decreasing of cumulated lp
        animateValue(cumulateLp, 0, text_cumulated_lp)

        if (mLpCalculator.mPlayerOneLp == 0 && mLpCalculator.mPlayerTwoLp != 0) {
            val snackBar = Snackbar.make(view, "Player 2 has won", Snackbar.LENGTH_LONG)
                    .setAction("RESET", { reset()} )
                    .setActionTextColor(Color.WHITE)
            snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYugiBlue))
            snackBar.show()
        }
        else if (mLpCalculator.mPlayerTwoLp == 0 && mLpCalculator.mPlayerOneLp != 0) {
            val snackBar = Snackbar.make(view, "Player 1 has won", Snackbar.LENGTH_LONG)
                    .setAction("RESET", { reset() })
                    .setActionTextColor(Color.WHITE)
            snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYugiBlue))
            snackBar.show()
        }
    }

    // Halve the life points
    fun halfButtonClicked(view: View) {
        text_cumulated_lp.text = "HALVE"
        mLpCalculator.mHalve = true
    }

    fun customButtomClicked(view: View) {
        val dialog = CustomLpFragment()
        dialog.show(supportFragmentManager, "CustomLpFragment")
    }

    // Clear the mCumulatedLp
    fun clearButtonClicked(view: View) {
        mLpCalculator.mHalve = false
        mLpCalculator.mCumulatedLp = 0
        text_cumulated_lp.text = mLpCalculator.mCumulatedLp.toString()
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

    fun toggleButtonClicked(view: View) {
        val toggleButton = view as ToggleButton
        if (toggleButton.isChecked) addOrSubtractToggle
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGain))
        else addOrSubtractToggle
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorLose))
    }
}
