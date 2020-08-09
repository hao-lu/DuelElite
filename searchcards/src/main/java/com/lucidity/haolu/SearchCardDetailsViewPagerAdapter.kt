package com.lucidity.haolu

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SearchCardDetailsViewPagerAdapter(fragment: Fragment, val cardName: String) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return if (position == 0) {
            val fragment = DetailsFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
                putString("cardName", cardName)
            }
            fragment
        } else if (position == 1) {
            val fragment = RulingsFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
                putString("cardName", cardName)
            }
            fragment
        } else {
            val fragment = TipsFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
                putString("cardName", cardName)
            }
            fragment
        }
    }
}