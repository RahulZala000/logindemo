package com.example.logindemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.logindemo.databinding.ActivityPasswordChangeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordChangeActivity : AppCompatActivity() {

    lateinit var binding:ActivityPasswordChangeBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient: GoogleSignInClient
    var loading=LoadingDialog(this)

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
                loading.startloading()
                var handler= Handler()
                handler.postDelayed(object : Runnable{
                    override fun run()
                    {
                        loading.isdismis()
                    }
                },500)
                Firebase.auth.currentUser!!.updatePassword(binding.signupemailnew.text.toString())
                    .addOnCompleteListener{
                        task->
                        if(task.isSuccessful)
                        {
                            i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    }

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