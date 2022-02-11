package com.example.logindemo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.Arrays.asList
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

class FacebookAuthActivity : AppCompatActivity() {

    lateinit var callbackManager:CallbackManager
    var auth = Firebase.auth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_auth)


        callbackManager = CallbackManager.Factory.create()
       // log_btn.setPermissions(Arrays.asList("email","user_birthday"));


     //   LoginManager.getInstance().logInWithReadPermissions(this,listOf("public_profile", "email"));

       // LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile"));

        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    handleFacebookAccessToken(loginResult!!.accessToken)
                }

                override fun onCancel() {
                  //  var i=Intent(this,M::class.java)
                }

                override fun onError(exception: FacebookException) {
                  //  var i=Intent(this,DashboardActivity::class.java)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                  //  updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        var i=Intent(this,MainActivity::class.java)
    }

}