package org.portfolio.contactcloud.ui

import android.content.Intent
import android.widget.Toast


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.portfolio.contactcloud.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var auth: FirebaseAuth
    val emailRegex = Regex(pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    val phoneRegex=Regex(pattern = "(\\+201\\d{9})|(201\\d{9})|(01\\d{9})")
    lateinit var gso: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //authentication initializing
        auth=Firebase.auth

        gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("733542834045-tlp93mpkt43hgv11m5jrvvnk2m656s56.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)

        binding.gmaillogin.setOnClickListener{
            Sign()
        }

        binding.signup.setOnClickListener{
            if(binding.email.text!!.isEmpty())
                binding.requiredEmail.visibility= View.VISIBLE

            if(binding.matchedPassword.text!!.isEmpty())
                binding.requiredMathchedPassword.visibility= View.VISIBLE

            if(binding.password.text!!.isEmpty())
                binding.requiredPassword.visibility= View.VISIBLE

            if(binding.phoneNumber.text!!.isEmpty())
                binding.requiredPhone.visibility= View.VISIBLE

             if (!isValidEmail(binding.email.text.toString()))
             {
                 binding.wrong.visibility=View.VISIBLE
                 binding.wrong.text="Please write the e-mail in the appropriate format"
             }

             else if (binding.password.text.toString().length<8) {
                 binding.wrong.visibility = View.VISIBLE
                 binding.wrong.text =
                     "Passwords should consist of more than 8 of both numbers and letters"
             }

             else if (!isValidPass(binding.password.text.toString())) {
                 binding.wrong.visibility = View.VISIBLE
                 binding.wrong.text = "Passwords should contain numbers and capital or small letters (a-z or A-Z)"
             }


            else if(binding.password.text.toString()!=binding.matchedPassword.text.toString()) {
                 binding.wrong.visibility = View.VISIBLE
                 binding.wrong.text = "Passwords are not matched"
             }



            else if(!isValidNumber(binding.phoneNumber.text.toString())) {
                 binding.wrong.visibility = View.VISIBLE
                 binding.wrong.text = "Wrong phone number"
             }

            else
            {
                auth.createUserWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var i =Intent(this@SignUp,Verification::class.java)
                                        i.putExtra("email",binding.email.text.toString())
                                        i.putExtra("password",binding.email.text.toString())

                                        startActivity(i)

                                    } else {
                                        binding.wrong.visibility = View.VISIBLE
                                        binding.wrong.text="Sorry something went wrong, please try again"
                                    }
                                }
                        }
                            else {
                                binding.wrong.visibility = View.VISIBLE
                                binding.wrong.text="this account is already created"

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
    private fun Sign() {
        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun FirebaseAuthWithGoogle(idToken: String) {
        var credential= GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful)
            {
                var user=auth.currentUser
                if(user!=null)
                {
                    val intent=Intent(this@SignUp,MainActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
            else
            {
                Toast.makeText(this,"Something went wrong, please try again",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task= GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account=task.getResult(ApiException::class.java)
                FirebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                Toast.makeText(applicationContext, "failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    companion object{
        const val RC_SIGN_IN=1001
        const val EXTRA_NAME="EXTRA_NAME"
    }

}