package org.portfolio.contactcloud.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.reset_password_email.view.*
import org.portfolio.contactcloud.GlobalSharedPreference
import org.portfolio.contactcloud.R
import org.portfolio.contactcloud.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth:FirebaseAuth
    lateinit var pref:GlobalSharedPreference
    lateinit var gso:GoogleSignInOptions
    lateinit var googleSignInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

         pref= GlobalSharedPreference.getInstance(this)
        auth=Firebase.auth
         gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("733542834045-tlp93mpkt43hgv11m5jrvvnk2m656s56.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)
        googleSignInClient.signOut()



        var virtalEmail=pref.getValue("email","none").toString()
        if(virtalEmail!="none")
        {
            binding.vis.visibility= View.INVISIBLE

            var alertBuilder=AlertDialog.Builder(this@Login)
            alertBuilder.setView(R.layout.prog_bar).setCancelable(false).create()
            var alertDialog=alertBuilder.show()

            var password=pref.getValue("password","none").toString()
            auth.signInWithEmailAndPassword(virtalEmail,password).addOnCompleteListener{
                if(it.isSuccessful)
                {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    alertDialog.dismiss()
                    startActivity(intent)

                    finish()
                } else
                {
                    binding.vis.visibility= View.VISIBLE
                    alertDialog.dismiss()

                }
            }

        }



        binding.gmailsign.setOnClickListener{

            Sign()
        }





        binding.forget.setOnClickListener{
            var alertBilder=AlertDialog.Builder(this)
            var view=layoutInflater.inflate(R.layout.reset_password_email,null,false)
            alertBilder.setView(view).create()
            var baseDialog=alertBilder.show()

            view.sendverif.setOnClickListener{
                if(view.emailverif.text.toString().isNotEmpty()) {
                    var build = AlertDialog.Builder(this).setTitle("Send E-mail reset")
                        .setMessage("Please click you to confirm password resetting")
                        .setPositiveButton("Confirm") { dialog, which ->
                            auth.sendPasswordResetEmail(view.emailverif.text.toString())
                            dialog.dismiss()
                            baseDialog.dismiss()
                            Snackbar.make(
                                binding.root,
                                "Check your mail to reset your password",
                                Snackbar.LENGTH_LONG
                            ).show()


                        }.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
                }
                else
                    view.requiredemail.visibility=View.VISIBLE
            }
            view.cancelverif.setOnClickListener{
                baseDialog.dismiss()
            }
        }




        binding.login.setOnClickListener{

            if(binding.email.text.toString().isNotEmpty()&&binding.password.text.toString().isNotEmpty())
                auth.signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        if(auth.currentUser!!.isEmailVerified)
                        {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            pref.setValue("email",binding.email.text.toString())
                            pref.setValue("password",binding.password.text.toString())
                            startActivity(intent)
                            finish()
                        }


                        else if (!auth.currentUser!!.isEmailVerified)

                        {
                            auth.currentUser!!.delete()

                            binding.emailWrong.visibility=View.VISIBLE

                        }




                    }
                    else
                    {
                        binding.emailWrong.visibility=View.VISIBLE
                    }

                }
            else
            {
                if(binding.email.text.toString().isEmpty())

                    binding.requiredEmail.visibility=View.VISIBLE

                if(binding.password.text.toString().isEmpty())

                    binding.requiredPassword.visibility=View.VISIBLE


            }
        }
        binding.signup.setOnClickListener {
            startActivity(Intent(this@Login,SignUp::class.java))
        }

    }

    private fun Sign() {
        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun FirebaseAuthWithGoogle(idToken: String) {
        var credential=GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful)
            {
                var user=auth.currentUser
                if(user!=null)
                {
                    val intent=Intent(this@Login,MainActivity::class.java)
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
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account=task.getResult(ApiException::class.java)
                FirebaseAuthWithGoogle(account.idToken!!)
            }catch (e:ApiException){
                Toast.makeText(applicationContext, "failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    companion object{
        const val RC_SIGN_IN=1001
        const val EXTRA_NAME="EXTRA_NAME"
    }


}