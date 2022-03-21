package com.example.logindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.logindemo.databinding.ActivityForgotpassBinding
import com.example.logindemo.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgotpassActivity : AppCompatActivity() {


    lateinit var binding: ActivityForgotpassBinding
    var i=intent
    var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)


    binding.signupClick.setOnClickListener{
        auth.sendPasswordResetEmail(binding.signupemail.text.toString())
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset Password Link Sent Successfully", Toast.LENGTH_SHORT)
                        .show()
                    i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
            }
    }

    }


}