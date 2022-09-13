package org.portfolio.contactcloud.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.portfolio.contactcloud.R

class splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            startActivity(Intent(this@splash_screen, login::class.java))
            finish()
        },4000)

        logo.animate().translationXBy(-1100f).duration=1000
        Handler().postDelayed({logo.animate().translationYBy(610f).duration=1000},1300)
    }
}