package org.portfolio.contactcloud.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.portfolio.contactcloud.R
import org.portfolio.contactcloud.databinding.ActivityLoginBinding
import org.portfolio.contactcloud.ui.login

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var preferences=getPreferences(MODE_PRIVATE)
        var editor=preferences.edit()
        auth=Firebase.auth


        var virtalEmail=preferences.getString("email","none").toString()
        if(virtalEmail!="none")
        {
            var password=preferences.getString("password","none").toString()
            auth.signInWithEmailAndPassword(virtalEmail,password)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

            finish()
        }




        binding.login.setOnClickListener{
            auth.signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString()).addOnCompleteListener{
                if(it.isSuccessful)
                {
                    if(auth.currentUser!!.isEmailVerified)
                    {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        editor.putString("email",binding.email.text.toString()).putString("password",binding.password.text.toString()).commit()
                        startActivity(intent)
                        finish()
                    Toast.makeText(applicationContext, "Welcome Dear", Toast.LENGTH_SHORT).show()}
                else
                    {
                        auth.currentUser!!.delete()

                        Toast.makeText(applicationContext, "E-mail or password is wrong", Toast.LENGTH_SHORT).show()
                    }




                }
                else
                    Toast.makeText(applicationContext, "E-mail or password is wrong", Toast.LENGTH_SHORT).show()

            }
        }
        binding.signup.setOnClickListener {
            startActivity(Intent(this@login,SignUp::class.java))
        }
        binding.forget.setOnClickListener{
            startActivity(Intent(this@login,ResetPassword::class.java))
        }
    }

}