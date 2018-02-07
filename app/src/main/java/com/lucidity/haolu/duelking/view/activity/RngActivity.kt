package com.lucidity.haolu.duelking.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lucidity.haolu.duelking.R
import com.lucidity.haolu.duelking.view.fragment.CoinFragment
import com.lucidity.haolu.duelking.view.fragment.DiceFragment
import kotlinx.android.synthetic.main.activity_log.*

/**
 * Holds the rng fragments: CoinFragment, DiceFragment
 */
class RngActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rng)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container_coin, CoinFragment())
        ft.replace(R.id.container_dice, DiceFragment())
        ft.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
