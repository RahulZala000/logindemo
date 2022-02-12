package com.example.logindemo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.logindemo.databinding.ActivityDashboardBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient= GoogleSignIn.getClient(this,gso)

        binding.email.text= Firebase.auth.currentUser!!.email

        binding.signoutClick.setOnClickListener {
             i = Intent(this, MainActivity::class.java)
            startActivity(i)

            Firebase.auth.signOut()
            googleclient.signOut()
            finish()
        }
        binding.emailclick.setOnClickListener {
            i = Intent(this, EmailChangeActivity::class.java)
            startActivity(i)
        }

        binding.passclick.setOnClickListener {
            i = Intent(this, PasswordChangeActivity::class.java)
            startActivity(i)
        }
    }
}