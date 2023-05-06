package com.example.muzix.view.onboarding

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.muzix.view.main.MainActivity
import com.example.muzix.R
import com.example.muzix.databinding.ActivityOnboardingBinding
import com.example.muzix.viewmodel.LoginViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private var client: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null
    private var callBackManager : CallbackManager? = null
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        //set up login google
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this, options)
        client?.signOut()
        auth = FirebaseAuth.getInstance()
        binding.btnLoginGoogle.setOnClickListener {
            signInGoogle()
        }
        //login facebook
        binding.btnLoginFB.setOnClickListener {
            callBackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, arrayListOf("public_profile"))
            LoginManager.getInstance().registerCallback(callBackManager, object :
                FacebookCallback<LoginResult> {
                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException) {
                    Log.e("lỗi",error.toString())
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }
            })
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth?.currentUser
                    updateUIFB(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUIFB(null)
                }
            }
    }

    private fun updateUIFB(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
            intent.putExtra("Email",user.displayName)
            viewModel.addInforUser()
            startActivity(intent)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager?.onActivityResult(requestCode,resultCode,data)
    }

    private fun signInGoogle() {
        val signInIntent = client?.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                intent.putExtra("Email", account.displayName)
                viewModel.addInforUser()
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}