package org.portfolio.contactcloud.ui

import android.content.Intent
import android.widget.Toast


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.portfolio.contactcloud.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var auth: FirebaseAuth
    val emailRegex = Regex(pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    val phoneRegex=Regex(pattern = "(\\+201\\d{9})|(201\\d{9})|(01\\d{9})")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //authentication initializing
        auth=Firebase.auth




        binding.signup.setOnClickListener{
            if(binding.email.text!!.isEmpty()||binding.password.text!!.isEmpty()||binding.phoneNumber.text!!.isEmpty())
                Toast.makeText(applicationContext, "Information isn't completed", Toast.LENGTH_SHORT).show()

            else if (!isValidEmail(binding.email.text.toString()))
                Toast.makeText(applicationContext, "Please write the e-mail in the appropriate format", Toast.LENGTH_SHORT).show()


            else if(binding.password.text.toString()!=binding.matchedPassword.text.toString())
                Toast.makeText(applicationContext, "Passwords not matched", Toast.LENGTH_SHORT).show()

            else if (binding.password.text.toString().length<8)
                Toast.makeText(applicationContext, "Passwords should consist of more than 8 of both numbers and letters", Toast.LENGTH_LONG).show()

            else if (!isValidPass(binding.password.text.toString()))
                Toast.makeText(applicationContext, "Passwords should contain numbers and alphabet letters ", Toast.LENGTH_SHORT).show()
            else if(!isValidNumber(binding.phoneNumber.text.toString()))
            {
                Toast.makeText(applicationContext, "Wrong phone number", Toast.LENGTH_SHORT).show()

            }
            else
            {
                auth.createUserWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var i =Intent(this@SignUp,verification::class.java)
                                        i.putExtra("email",binding.email.text.toString())
                                        i.putExtra("password",binding.email.text.toString())

                                        startActivity(i)

                                    } else {
                                        Toast.makeText(applicationContext, "Sorry something went wrong, please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                            else {

                            Toast.makeText(applicationContext, "this account is already created", Toast.LENGTH_SHORT).show()
                        }
                    }




                }
            }
        }


    fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }


    fun isValidNumber(number:String): Boolean {
        return phoneRegex.matches(number)
    }


    fun isValidPass(email: String): Boolean {
        var num=IntArray(2)
        for (i in email)
        {
            if(i.isDigit())
            {
                num[0]+=1
            }
            else if(i.isLetter())
                num[1]+=1

            if(num[0]>0&&num[1]>0)
                return true

        }

        return false
    }
}