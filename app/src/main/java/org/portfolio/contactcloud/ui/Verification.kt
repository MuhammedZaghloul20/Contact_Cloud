package org.portfolio.contactcloud.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import org.portfolio.contactcloud.GlobalSharedPreference
import org.portfolio.contactcloud.R

class Verification : AppCompatActivity() {
    lateinit var handler: Handler
    lateinit var runnable:Runnable
    val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        var email=intent.getStringExtra("email").toString()
        var password=intent.getStringExtra("password").toString()
        val pref=GlobalSharedPreference.getInstance(this)

         handler = Handler()
         runnable = object : Runnable {
            override fun run() {
                val user = FirebaseAuth.getInstance().currentUser
                user?.reload()?.addOnSuccessListener {
                    if (user.isEmailVerified) {
                        val intent = Intent(this@Verification, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        handler.removeCallbacks(runnable)
                        pref.setValue("email",email)
                        pref.setValue("password",password)
                        startActivity(intent)
                        finishAffinity()

                    }
                }
                handler.postDelayed(this, 5000) // run the runnable again after 5 seconds
            }
        }

// start the runnable
        handler.post(runnable)




        Handler().postDelayed({
            auth.currentUser!!.delete()
            finish()
        },60000)
    }


    override fun onDestroy() {
        super.onDestroy()
    handler.removeCallbacks(runnable)
    }

}

