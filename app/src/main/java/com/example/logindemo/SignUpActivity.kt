package com.example.logindemo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.example.logindemo.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.facebook.FacebookException

import com.facebook.login.LoginResult

import com.facebook.FacebookCallback

import com.facebook.login.LoginManager

import com.facebook.CallbackManager
import com.google.firebase.firestore.FirebaseFirestore
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.firestore.DocumentReference

import com.google.android.gms.tasks.OnSuccessListener





class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient:GoogleSignInClient
    var  db: FirebaseFirestore? = FirebaseFirestore.getInstance()

    companion object{
        const val sign=25
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient=GoogleSignIn.getClient(this,gso)

        binding.signupClick.setOnClickListener {

            i = Intent(this, MainActivity::class.java)

            if(infocheck()) {
                signprocess()
            }
        }

        binding.signgoogle.setOnClickListener{
            i = Intent(this, MainActivity::class.java)

            signgoogle()
        }
        binding.signfacebook.setOnClickListener{
            i = Intent(this,FacebookAuthActivity::class.java)

            startActivity(i)
        }

    }


    private fun signgoogle()
    {
        var signIntent=googleclient.signInIntent
        startActivityForResult(signIntent, sign)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== sign)
        {
            var task=GoogleSignIn.getSignedInAccountFromIntent(data)

            try
            {
                var account=task.getResult(ApiException::class.java)
                doAuth(account!!.idToken)
            }
            catch(e:ApiException)
            {
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doAuth(idToken:String?)
    {
        var credentials=GoogleAuthProvider.getCredential(idToken,null)

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
        var email = binding.signupemail.text.toString()
        var pass = binding.signuppass.text.toString()


        var user= hashMapOf(
            "Email" to email,
            "Password" to pass
        )

        var users=db!!.collection("User")
        var query=users.whereEqualTo("Email",email).get()
            .addOnSuccessListener {
                    it->
                if(it.isEmpty)
                {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                var user = auth.currentUser
                                user!!.sendEmailVerification()
                                    .addOnCompleteListener(this)
                                    { task ->
                                        if (task.isSuccessful) {

                                            users.document(email).set(user)
                                            Toast.makeText(this, "email sent is successfully", Toast.LENGTH_SHORT).show()
                                            startActivity(i)
                                        }
                                    }
                            }
                        }
                }
                else{
                    Toast.makeText(this,"email already register",Toast.LENGTH_SHORT).show()
                    startActivity(i)
                }
            }

    }

    private fun infocheck(): Boolean {
        var email:String=binding.signupemail.text.toString()
        var pass:String=binding.signuppass.text.toString()

        if((email=="")&&(pass==""))
        {
            binding.signupemail.setError("Input name")
            binding.signuppass.setError("Input Password")
            return false
        }
        else if(email=="") {
            binding.signupemail.setError("Input name")
            return false
        }
        else if(pass=="") {
            binding.signuppass.setError("Input Password")
            return false
        }
        else
        {
            return true
        }
    }
}