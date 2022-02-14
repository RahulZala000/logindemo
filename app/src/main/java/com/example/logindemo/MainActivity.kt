package com.example.logindemo

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.logindemo.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.internal.TextDrawableHelper
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var i=intent
    var auth = Firebase.auth
    lateinit var googleclient: GoogleSignInClient

    companion object{
        const val sign=25
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth.

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient=GoogleSignIn.getClient(this,gso)


        if(auth.currentUser!=null)
        {
            i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
        }
        binding.newUser.setOnClickListener {
            i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
        binding.loginClick.setOnClickListener{
            i = Intent(this, DashboardActivity::class.java)
           if(infocheck())
           {
               signprocess()
           }
        }
        binding.forgotClick.setOnClickListener{
            i = Intent(this, ForgotpassActivity::class.java)
            startActivity(i)
        }
        binding.signgoogle.setOnClickListener{
            i = Intent(this, DashboardActivity::class.java)

            signgoogle()
        }
    }

    private fun signgoogle()
    {
        var signIntent=googleclient.signInIntent
        startActivityForResult(signIntent, SignUpActivity.sign)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== SignUpActivity.sign)
        {
            var task= GoogleSignIn.getSignedInAccountFromIntent(data)

            try
            {
                var account=task.getResult(ApiException::class.java)
                doAuth(account!!.idToken)
            }
            catch(e: ApiException)
            {
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doAuth(idToken:String?)
    {
        var credentials= GoogleAuthProvider.getCredential(idToken,null)

        auth.signInWithCredential(credentials)
            .addOnCompleteListener(this){
                    task->
                if(task.isSuccessful)
                {
                    startActivity(i)
                }
                else
                {
                    Toast.makeText(this,"Authantication is Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun signprocess() {
        var email: String = binding.signemail.text.toString()
        var pass: String = binding.signpass.text.toString()

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful)
                {
                    if(auth.currentUser!!.isEmailVerified)
                    {
                        startActivity(i)
                    }
                    else
                    {
                        Toast.makeText(this,"Your Email Is Not Verified",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun infocheck(): Boolean {
        var email:String=binding.signemail.text.toString()
        var pass:String=binding.signpass.text.toString()

        if((email=="")&&(pass==""))
        {
            binding.signemail.setError("Input name")
            binding.signpass.setError("Input Password")
            return false
        }
        else if(email=="") {
            binding.signemail.setError("Input name")
            return false
        }
        else if(pass=="") {
            binding.signpass.setError("Input Password")
            return false
        }
        else
        {
            return true
        }
    }
}