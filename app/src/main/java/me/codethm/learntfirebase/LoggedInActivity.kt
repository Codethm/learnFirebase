package me.codethm.learntfirebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logged_in.*
import android.support.design.widget.Snackbar
import android.content.Intent


class LoggedInActivity : AppCompatActivity() {

    private var fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        val intent = getIntent()

        logoutButton.setOnClickListener { v: View ->
            showMessage(v, "Logging Out...")
            signOut()
        }

        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                this.finish()
            } else {
                val str = intent.getStringExtra("id") + "\n" + fbAuth.uid + "\n" + fbAuth.currentUser?.displayName
                userDetail.text = str
            }
        }
    }

    private fun signOut() {
        fbAuth.signOut()
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}