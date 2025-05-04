package com.project.phycare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth



class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        auth = FirebaseAuth.getInstance()
        val emailet = findViewById<EditText>(R.id.emailInput)
        val passwordet = findViewById<EditText>(R.id.passwordInput)
        val signinbtn = findViewById<Button>(R.id.signInButton)
        val currenuser = auth.currentUser
        val signupredirect = findViewById<TextView>(R.id.signupRedirect)
        val webview=findViewById<WebView>(R.id.webview)
        if (currenuser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            webview.settings.javaScriptEnabled=true
            webview.loadUrl("https://my.spline.design/worldplanet-bik5EOcrvBZZSrinSwT29qrG/")
            signinbtn.setOnClickListener {
                if (emailet.text.toString() == "" || passwordet.text.toString() == "") {
                    Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(
                        emailet.text.toString(),
                        passwordet.text.toString()
                    ).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Toast.makeText(
                                this,
                                "Error->${task.exception?.message.toString()}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }


            }
        }
        signupredirect.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }
}