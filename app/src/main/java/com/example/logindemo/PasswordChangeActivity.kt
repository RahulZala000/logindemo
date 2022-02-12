package com.example.logindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.logindemo.databinding.ActivityEmailChangeBinding
import com.example.logindemo.databinding.ActivityPasswordChangeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase

class PasswordChangeActivity : AppCompatActivity() {

    lateinit var binding:ActivityPasswordChangeBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient= GoogleSignIn.getClient(this,gso)



        var newPass=binding.signupemailnew.text.toString()
        binding.passclick.setOnClickListener{

            if(infocheck())
            {
                Firebase.auth.currentUser!!.updatePassword(newPass)
                i = Intent(this, DashboardActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun infocheck(): Boolean {
        var pass=binding.signupemailnew.text.toString()

        if(pass=="")
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