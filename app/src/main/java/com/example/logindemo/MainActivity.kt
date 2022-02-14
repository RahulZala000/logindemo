package com.example.logindemo

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
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
import androidx.annotation.NonNull
import com.facebook.appevents.ml.ModelManager

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.auth.AuthResult

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var i=intent
    var auth = Firebase.auth
    lateinit var googleclient: GoogleSignInClient
    lateinit var loginprogress:ProgressBar
    var  db:FirebaseFirestore? = FirebaseFirestore.getInstance()

    companion object{
        const val sign=25
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
        binding.signtwitter.setOnClickListener{
            Task->

            val pendingResultTask: Task<AuthResult> = auth.getPendingAuthResult() as Task<AuthResult>
            if (pendingResultTask != null) {
                // There's something already here! Finish the sign-in for your user.
                pendingResultTask
                    .addOnSuccessListener(


                        OnSuccessListener<AuthResult?> {

                            i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)
                            // User is signed in.
                            // IdP data available in
                            // authResult.getAdditionalUserInfo().getProfile().
                            // The OAuth access token can also be retrieved:
                            // authResult.getCredential().getAccessToken().
                            // The OAuth secret can be retrieved by calling:
                            // authResult.getCredential().getSecret().
                        })
                    .addOnFailureListener(
                        OnFailureListener {
                            // Handle failure.
                        })
            } else {
                // There's no pending result so you need to start the sign-in flow.
                // See below.
            }
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
                    else if(email!=auth.currentUser!!.email)
                    {
                        Toast.makeText(this,"User is Not Registed",Toast.LENGTH_SHORT).show()
                        binding.signemail.setError("Enter Valid Email Address")
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
            binding.signemail.setError("Input Email")
            binding.signpass.setError("Input Password")
            return false
        }
        else if(email=="") {
            binding.signemail.setError("Input Email")
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