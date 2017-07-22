package com.example.haolu.duelmaster

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.haolu.duelmaster.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var LIFE_POINT_CALCULATOR = LifePointCalculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        DataBindingUtil.setContentView<ContentMainBinding>(this, R.layout.activity_main)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Search YuGiOh Cards on Wikia", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            LIFE_POINT_CALCULATOR.printLog()
            val intent = Intent(this, SearchCardActivity::class.java)
            startActivity(intent)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        binding.appBarMain.contentMain.setVariable(BR.LPCalculator, LIFE_POINT_CALCULATOR)
        binding.appBarMain.contentMain.executePendingBindings()

    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
            return true
        }
        else if (id == R.id.action_pause) {
            return true
        }
        else if (id == R.id.action_reset) {
            LIFE_POINT_CALCULATOR.reset()
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

    fun numberButtonPressed(view: View) {
        if (view is TextView) LIFE_POINT_CALCULATOR.cumulatedLP += view.text.toString().toInt()
//        if (view is TextView) LIFE_POINT_CALCULATOR.updateCumulatedLP(view.text.toString().toDouble())
        println(LIFE_POINT_CALCULATOR.cumulatedLP)
    }


    fun playerButtonPressed(view: View) {
        if (view.id == R.id.player_one_btn) {
            LIFE_POINT_CALCULATOR.updateLP(addOrSubtractToggle.isChecked, true)
            println(LIFE_POINT_CALCULATOR.playerOneLP)
        }
        else {
            LIFE_POINT_CALCULATOR.updateLP(addOrSubtractToggle.isChecked, false)
            println(LIFE_POINT_CALCULATOR.playerTwoLP)
        }
    }

    fun logButtonPressed(view: View) {
        val intent = Intent(view.context, LogActivity::class.java)
        intent.putExtra("log", LIFE_POINT_CALCULATOR.log)
        startActivity(intent)
    }

    fun halfButtonPressed(view: View) {
        cumulated_lp.text = "HALVE"
        LIFE_POINT_CALCULATOR.halve = true
    }

    fun clearButtonPressed(view: View) {
        LIFE_POINT_CALCULATOR.halve = false
        LIFE_POINT_CALCULATOR.cumulatedLP = 0
    }


}
