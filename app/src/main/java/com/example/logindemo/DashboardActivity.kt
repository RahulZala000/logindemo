package com.example.logindemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.logindemo.databinding.ActivityDashboardBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient: GoogleSignInClient
    var  db: FirebaseFirestore? = FirebaseFirestore.getInstance()
    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var loading=LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var sharedPref=getSharedPreferences("Email",Context.MODE_PRIVATE)
        var editor=sharedPref.edit()

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleclient= GoogleSignIn.getClient(this,gso)

        binding.email.text= Firebase.auth.currentUser!!.email


          editor.apply{

               putString("email", binding.email.text.toString())
              apply()
          }

        binding.signoutClick.setOnClickListener {
            sharedPref.edit().remove("email").apply()
             i = Intent(this, MainActivity::class.java)
            startActivity(i)


            Firebase.auth.signOut()
            googleclient.signOut()
            finish()
        }
        binding.emailclick.setOnClickListener { loading.startloading()
            var handler= Handler()
            handler.postDelayed(object : Runnable{
                override fun run()
                {
                    loading.isdismis()
                }
            },1000)

            i = Intent(this, EmailChangeActivity::class.java)
            startActivity(i)
        }

        binding.passclick.setOnClickListener {
            loading.startloading()
            var handler= Handler()
            handler.postDelayed(object : Runnable{
                override fun run()
                {
                    loading.isdismis()
                }
            },1000)

            i = Intent(this, PasswordChangeActivity::class.java)
            startActivity(i)
        }
    }
    private fun setText(islogin: String?) {
        db!!.collection("Users").document("Email").get()
            .addOnCompleteListener{
                    task->
                if(task.isSuccessful)
                {
                //    binding.email.text=sharedPref.getString("email",null)
                    //binding.email.text= Firebase.auth.currentUser!!.email
                }
            }

    }


}