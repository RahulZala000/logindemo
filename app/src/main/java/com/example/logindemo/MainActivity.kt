package com.example.logindemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.logindemo.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var i=intent
    var auth = Firebase.auth
    lateinit var googleclient: GoogleSignInClient
    lateinit var loginprogress:ProgressBar
    var  db:FirebaseFirestore? = FirebaseFirestore.getInstance()
    lateinit var sharedPref:SharedPreferences
    private  var editor:SharedPreferences.Editor?=null
    companion object{
        const val sign=25
    }
    lateinit var progress:ProgressBar
    private lateinit var isdialog:AlertDialog
    var loading=LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var sharedPref=getSharedPreferences("Email",Context.MODE_PRIVATE)
        var editor=sharedPref.edit()



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
            finish()
        }


        binding.newUser.setOnClickListener {

            i = Intent(this, SignUpActivity::class.java)
            startActivity(i)

        }
        binding.loginClick.setOnClickListener{


            i = Intent(this, DashboardActivity::class.java)
           if(infocheck())
           {
               loading.startloading()
               var handler=Handler()
               handler.postDelayed(object : Runnable{
                   override fun run()
                   {
                       loading.isdismis()
                   }
               },1000)

               signprocess()
           }
        }
        binding.forgotClick.setOnClickListener{
            loading.startloading()
            var handler=Handler()
            handler.postDelayed(object : Runnable{
                override fun run()
                {
                    loading.isdismis()
                }
            },500)

            i = Intent(this, ForgotpassActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.signgoogle.setOnClickListener{
            loading.startloading()
            var handler=Handler()
            handler.postDelayed(object : Runnable{
                override fun run()
                {
                    loading.isdismis()
                }
            },1000)

            i = Intent(this, PasswordChangeActivity::class.java)
            signgoogle()
        }

    }



    private fun signgoogle()
    {
        var signIntent=googleclient.signInIntent
        startActivityForResult(signIntent, sign)
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
                  //  i.putExtra("email", email)
                    editor?.apply{
                        putString("email",email)
                        apply()
                    }
                    startActivity(i)
                    finish()
                }
                else if(task.isComplete)
                {
                         Toast.makeText(this, "Plz Enter Valid Email And Password", Toast.LENGTH_SHORT).show()
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


