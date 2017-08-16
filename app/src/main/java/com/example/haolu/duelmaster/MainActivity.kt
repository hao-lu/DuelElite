package com.example.haolu.duelmaster

import android.animation.ValueAnimator
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
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
import android.widget.Toast
import android.widget.ToggleButton
import com.example.haolu.duelmaster.databinding.AppBarMainBinding
import kotlinx.android.synthetic.main.content_main.*
import io.realm.Realm
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CustomLpFragment.CustomLpDialogListener {

    var LIFE_POINT_CALCULATOR = LifePointCalculator()
    var mRealm = Realm.getDefaultInstance()

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
        binding.contentMain.setVariable(BR.LPCalculator, LIFE_POINT_CALCULATOR)
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_start) {
            val timer = object: CountDownTimer(5000, 1000) {
                override fun onFinish() {
                    text_duel_time.text = "Round Over"
                }

                override fun onTick(millisUntilFinished: Long) {
                    val formatted = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    )
                    text_duel_time.text = formatted
                }
            }.start()
            return true
        }
        else if (id == R.id.action_pause) {
            return true
        }
        else if (id == R.id.action_reset) {
            LIFE_POINT_CALCULATOR.reset()
            text_player_one_lp.text = "8000"
            text_player_two_lp.text = "8000"
            text_cumulated_lp.text = "0"
        }

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
        val currLp = LIFE_POINT_CALCULATOR.cumulatedLp
        val editText = dialog.dialog.findViewById(R.id.edit_custom) as EditText
        LIFE_POINT_CALCULATOR.cumulatedLp += editText.text.toString().toInt()
        val newLp = LIFE_POINT_CALCULATOR.cumulatedLp
        animateValue(currLp, newLp, text_cumulated_lp)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // Cancel
    }

    private fun animateValue(currLp: Int, newLp: Int, text: TextView) {
        val animator = ValueAnimator.ofInt(currLp, newLp)
        animator.duration = 500
        animator.addUpdateListener { text.text = it.animatedValue.toString() }
        animator.start()
    }

    fun numberButtonPressed(view: View) {
        val currLp = LIFE_POINT_CALCULATOR.cumulatedLp
        // Update the cumulatedLp
        if (view is TextView) LIFE_POINT_CALCULATOR.cumulatedLp += view.text.toString().toInt()
        val newLp = LIFE_POINT_CALCULATOR.cumulatedLp
        animateValue(currLp, newLp, text_cumulated_lp)
    }

    // Update the player life points (gain/lose)
    fun playerButtonPressed(view: View) {
        val currLp: Int
        val newLp: Int
        val currPlayer: TextView
        val cumulateLp = LIFE_POINT_CALCULATOR.cumulatedLp

        if (view.id == R.id.button_player_one) {
            currLp = LIFE_POINT_CALCULATOR.playerOneLp
            currPlayer = text_player_one_lp
            LIFE_POINT_CALCULATOR.updateLP(addOrSubtractToggle.isChecked, true)
            newLp = LIFE_POINT_CALCULATOR.playerOneLp
        }
        else {
            currLp = LIFE_POINT_CALCULATOR.playerTwoLp
            currPlayer = text_player_two_lp
            LIFE_POINT_CALCULATOR.updateLP(addOrSubtractToggle.isChecked, false)
            newLp = LIFE_POINT_CALCULATOR.playerTwoLp
        }

        // Animate player losing life points
        animateValue(currLp, newLp, currPlayer)
        // Animate decreasing of cumulated lp
        animateValue(cumulateLp, 0, text_cumulated_lp)
    }

    // Halve the life points
    fun halfButtonPressed(view: View) {
        text_cumulated_lp.text = "HALVE"
        LIFE_POINT_CALCULATOR.halve = true
    }

    fun customButtonPressed(view: View) {
//        val fm = supportFragmentManager
//        val customLpFragment = CustomLpFragment()
//        customLpFragment.show(fm, "fragment_custom_lp")
        val dialog = CustomLpFragment()
        dialog.show(supportFragmentManager, "CustomLpFragment")
    }

    // Clear the cumulatedLp
    fun clearButtonPressed(view: View) {
        LIFE_POINT_CALCULATOR.halve = false
        LIFE_POINT_CALCULATOR.cumulatedLp = 0
        text_cumulated_lp.text = LIFE_POINT_CALCULATOR.cumulatedLp.toString()
    }

    // Launch mLog activity
    fun logButtonPressed(view: View) {
        val intent = Intent(view.context, LogActivity::class.java)
        intent.putExtra("mLog", LIFE_POINT_CALCULATOR.log)
        startActivity(intent)
    }

    fun flipButtonPressed(view: View) {
//        Toast.makeText(this, "Roll dice / flip coin", Toast.LENGTH_SHORT).show()
//        val dialog = CoinFlipFragment()
//        dialog.show(supportFragmentManager, "CoinFlipFragment")
        val fragment = CoinFlipFragment()
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(android.R.id.content, fragment).addToBackStack(null).commit()
    }

    fun rollButtonPressed(view: View) {
        val fragment = RollDiceFragment()
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(android.R.id.content, fragment).addToBackStack(null).commit()
    }

    fun toggleButtonPressed(view: View) {
        val toggleButton = view as ToggleButton
        if (toggleButton.isChecked) addOrSubtractToggle.
                setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGain))
        else addOrSubtractToggle.
                setTextColor(ContextCompat.getColor(applicationContext, R.color.colorLose))
    }

    fun captureFragmentTouches(view: View) {

    }

    fun dismissFragment(view: View) {
        supportFragmentManager.popBackStack()
    }

}
