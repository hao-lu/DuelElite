package com.lucidity.haolu.searchcards.view.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lucidity.haolu.searchcards.view.fragment.CardTipsFragment
import com.lucidity.haolu.searchcards.view.fragment.CardInformationFragment
import com.lucidity.haolu.searchcards.view.fragment.CardRulingsFragment

class SearchCardDetailsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return if (position == 0) {
            val fragment = CardInformationFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
//                putString("cardName", cardName)
            }
            fragment
        } else if (position == 1) {
            val fragment = CardRulingsFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
//                putString("cardName", cardName)
            }
            fragment
        } else {
            val fragment = CardTipsFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt("ARG_OBJECT", position + 1)
//                putString("cardName", cardName)
            }
            fragment
        }
    }
}