package org.portfolio.contactcloud.ui.onBoarding

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.android.synthetic.main.fragment_view_pager.view.*
import org.portfolio.contactcloud.Adapters.ViewPagerAdapter
import org.portfolio.contactcloud.R

class ViewPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_view_pager, container, false)

        val list= arrayOf(First(),Second())
        view.viewPager.adapter=ViewPagerAdapter(list, requireActivity().supportFragmentManager,lifecycle )
        view.tab.addTab(view.tab.newTab().setCustomView(R.layout.taplayout_custom_view))
        view.tab.addTab(view.tab.newTab().setCustomView(R.layout.taplayout_custom_view))



        TabLayoutMediator(view.tab, view.viewPager) { tab, position ->
            if(position==0)
                tab.setCustomView(R.layout.taplayout_custom_view2)
            else
            tab.setCustomView(R.layout.taplayout_custom_view)
        }.attach()

        view.tab.addOnTabSelectedListener(object :OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.setCustomView(R.layout.taplayout_custom_view2)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.setCustomView(R.layout.taplayout_custom_view)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

//        }
        return view
    }

}