package com.example.logindemo



import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.logindemo.databinding.ActivitySignupBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase



class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    var auth = Firebase.auth
    var i = intent
    lateinit var googleclient:GoogleSignInClient
    var  db: FirebaseFirestore? = FirebaseFirestore.getInstance()
    var loading=LoadingDialog(this)
    companion object{
        const val sign=25
    }

    private lateinit var callbackManager: CallbackManager

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

            if(infocheck()) {
                loading.startloading()
                var handler= Handler()
                handler.postDelayed(object : Runnable{
                    override fun run()
                    {
                        loading.isdismis()
                    }
                },1000)

                signprocess()
            }
        }

        binding.signgoogle.setOnClickListener{
            loading.startloading()
            var handler= Handler()
            handler.postDelayed(object : Runnable{
                override fun run()
                {
                    loading.isdismis()
                }
            },1000)

            i = Intent(this, DashboardActivity::class.java)

            signgoogle()
        }
        binding.loginButton.setOnClickListener{

            Toast.makeText(baseContext,"click",Toast.LENGTH_SHORT).show()

            callbackManager = CallbackManager.Factory.create()

            binding.loginButton.setPermissions(listOf("email", "user_birthday"))

           binding.loginButton.registerCallback(callbackManager, object :
               FacebookCallback<LoginResult>{
               override fun onSuccess(result: LoginResult?) {
                    Log.d("@facebook","Success")
                  // i=Intent(this@SignUpActivity,DashboardActivity::class.java)
                   handleFacebookAccessToken(result!!.accessToken)
                 //  startActivity(i)
               }

               override fun onCancel() {
                   Toast.makeText(this@SignUpActivity,"canser",Toast.LENGTH_SHORT).show()
               }

               override fun onError(error: FacebookException?) {
                   Toast.makeText(this@SignUpActivity,"error",Toast.LENGTH_SHORT).show()
               }

           })
        }

    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    i= Intent(this,DashboardActivity::class.java)
                    startActivity(i)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                   // updateUI(null)
                }
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
                            if (task.isSuccessful)
                            {
                                i = Intent(this, DashboardActivity::class.java)
                                users.document(email).set(user)
                              //  i.putExtra("email",email)
                                Toast.makeText(this, "email sent is successfully", Toast.LENGTH_SHORT).show()
                                startActivity(i)
                            }
                        }
                }
                else
                {
                    i = Intent(this, MainActivity::class.java)
                    Toast.makeText(this,"Email Already Register",Toast.LENGTH_SHORT).show()
                    startActivity(i)
                }
            }

    }

    private fun infocheck(): Boolean {
        var email:String=binding.signupemail.text.toString()
        var pass:String=binding.signuppass.text.toString()

        if((email=="")&&(pass==""))
        {
            binding.signupemail.setError("Input Email")
            binding.signuppass.setError("Input Password")
            return false
        }
        else if(email=="") {
            binding.signupemail.setError("Input Email")
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