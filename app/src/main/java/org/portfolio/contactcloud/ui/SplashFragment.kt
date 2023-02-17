package org.portfolio.contactcloud.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_splash.view.*
import org.portfolio.contactcloud.GlobalSharedPreference
import org.portfolio.contactcloud.R

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_splash, container, false)
        Handler().postDelayed({
            val pref=GlobalSharedPreference.getInstance(view.context)
            val check=pref.getValue("firstTime","yes")
            if(check=="yes")
           findNavController().navigate(R.id.action_splash_Fragment_to_viewPager2)
            else{
                val intent=Intent(activity, Login::class.java)
                startActivity(intent)
                activity?.finish()
            }
        },4000)

        view.logo.animate().translationXBy(-1100f).duration=800
        Handler().postDelayed({view.logo.animate().translationYBy(610f).duration=800},1100)
        Handler().postDelayed({view.logo.animate().scaleX(.8f).scaleY(0.8f).duration=700},2200)

        return view
    }
}