package com.carlydean.test.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.carlydean.test.ui.fragments.FavoritesFragment
import com.carlydean.test.ui.fragments.LibraryFragment
import com.carlydean.test.ui.fragments.ReadingNowFragment

/**
 * Adapter para ViewPager2 que maneja los 3 tabs
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LibraryFragment.newInstance()
            1 -> FavoritesFragment.newInstance()
            2 -> ReadingNowFragment.newInstance()
            else -> LibraryFragment.newInstance()
        }
    }
}
