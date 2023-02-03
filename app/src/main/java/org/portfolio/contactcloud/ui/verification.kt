package org.portfolio.contactcloud.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import org.portfolio.contactcloud.R

class verification : AppCompatActivity() {

    val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        var email=intent.getStringExtra("email").toString()
        var password=intent.getStringExtra("password").toString()


        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    user.reload().addOnSuccessListener {
                        if (user.isEmailVerified) {
                            val intent = Intent(this@verification, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()

                        }
                    }
                }
                handler.postDelayed(this, 5000) // run the runnable again after 5 seconds
            }
        }

// start the runnable
        handler.post(runnable)




        Handler().postDelayed({finish()
            auth.currentUser!!.delete()
        },60000)
    }




}

