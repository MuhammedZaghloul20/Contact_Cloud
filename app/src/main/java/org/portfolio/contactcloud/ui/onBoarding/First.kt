package org.portfolio.contactcloud.ui.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_first.view.*
import org.portfolio.contactcloud.R


class First() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_first, container, false)

        view.imageView2.animate().translationX(-40f).alpha(1f).duration=1000
        view.textView.animate().alpha(1f).duration=1500
        view.textView2.animate().translationX(-20f).alpha(1f).duration=2000
        view.next.setOnClickListener{
            val viewPager=activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.setCurrentItem(1,true)

        }
        view.next.animate().alpha(1f).duration=2000
        val call=object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(call)
        return view
    }



}