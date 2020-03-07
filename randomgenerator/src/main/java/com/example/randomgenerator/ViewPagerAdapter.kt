package com.example.randomgenerator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = CoinFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("ARG_OBJECT", position + 1)
        }
        return fragment
    }
}