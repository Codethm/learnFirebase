package me.codethm.learntfirebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var mAuth = FirebaseAuth.getInstance()
    private var mGoogleApiClient: GoogleApiClient? = null
    val RC_SIGN_IN = 9001


    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("Dev", "onConnectionFailed:" + connectionResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        loginButton.setOnClickListener { v: View ->
            signIn(v, editEmail.text.toString(), editPassword.text.toString())
        }

        createAccountButton.setOnClickListener { v: View ->
            createAccount(v, editEmail.text.toString(), editPassword.text.toString())
        }

        google_button.setOnClickListener { v ->
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Log.d("Dev", "firebaseAuthWithGoogle Successful")
                        val intent = Intent(this, LoggedInActivity::class.java)
                        intent.putExtra("id", mAuth.currentUser?.email)
                        startActivity(intent)
                    } else {
                        Log.d("Dev", "firebaseAuthWithGoogle ${task.exception?.message}")
                        showMessage(main_layout, "Error: ${task.exception?.message}")
                    }
                })
    }

    private fun signIn(view: View, email: String, password: String) {
        showMessage(view, "Authenticating...")

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                Log.d("Dev", "Sign in Successful")
                val intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", mAuth.currentUser?.email)
                startActivity(intent)

            } else {
                Log.d("Dev", task.exception?.message)
                showMessage(view, "Error: ${task.exception?.message}")
            }
        })
    }

    private fun createAccount(view: View, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", mAuth.currentUser?.email)
                startActivity(intent)

            } else {
                showMessage(view, "Error: ${task.exception?.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            firebaseAuthWithGoogle(result.signInAccount!!)

        }
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}
