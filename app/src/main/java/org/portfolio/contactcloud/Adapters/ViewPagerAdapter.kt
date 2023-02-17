package org.portfolio.contactcloud.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(val fragments:Array<Fragment>,fm:FragmentManager,lifeCycle:Lifecycle): FragmentStateAdapter(fm,lifeCycle) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.get(position)
    }
}