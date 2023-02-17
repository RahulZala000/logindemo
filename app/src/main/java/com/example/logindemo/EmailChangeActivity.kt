package com.example.logindemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logindemo.databinding.ActivityEmailChangeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailChangeActivity : AppCompatActivity() {

    lateinit var binding:ActivityEmailChangeBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient: GoogleSignInClient
    var loading=LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEmailChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient= GoogleSignIn.getClient(this,gso)

        binding.signupemailold.setText( Firebase.auth.currentUser!!.email)

        var newEmail=binding.signupemailnew.text.toString()
        binding.emailclick.setOnClickListener{

           if(infocheck())
           {
               loading.startloading()
               var handler= Handler()
               handler.postDelayed(object : Runnable{
                   override fun run()
                   {
                       loading.isdismis()
                   }
               },1000)

                auth.currentUser!!.updateEmail(binding.signupemailnew.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email Is Updte", Toast.LENGTH_SHORT).show()
                        i = Intent(this, DashboardActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    if(task.isComplete)
                    {
                        Toast.makeText(this, "Email Is Already Register", Toast.LENGTH_SHORT).show()
                    }
                }
           }

            }
        }

    private fun infocheck(): Boolean {
        var email=binding.signupemailnew.text.toString()

        if(email=="")
        {
            binding.signupemailnew.setError("Plz Enter The New Email")
            return false
        }
        else
        {
            return true
        }
    }


}