package org.portfolio.contactcloud.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.portfolio.contactcloud.R
import org.portfolio.contactcloud.ui.login

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener{
            startActivity(Intent(this@login,MainActivity::class.java))
        }
    }
}