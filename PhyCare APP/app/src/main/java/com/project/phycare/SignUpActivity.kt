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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

lateinit var username: String
class SignUpActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase:DatabaseReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        firebaseAuth= FirebaseAuth.getInstance()
        firebaseDatabase=FirebaseDatabase.getInstance().reference
        val emailet=findViewById<EditText>(R.id.emailInput)
        val passwordet=findViewById<EditText>(R.id.passwordInput)
        val signupbtn=findViewById<Button>(R.id.signUpButton)
        val currenuser=firebaseAuth.currentUser
        val webview=findViewById<WebView>(R.id.webview)
        val signinredirect=findViewById<TextView>(R.id.signinRedirect)
        if(currenuser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else {
            webview.settings.javaScriptEnabled=true
            webview.loadUrl("https://my.spline.design/unchained-7ZZNf9mB76Rl8nxWJ30blymk/")
            signupbtn.setOnClickListener {
                if (emailet.text.toString() ==""|| passwordet.text.toString() == "") {
                    Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(
                        emailet.text.toString(),
                        passwordet.text.toString()
                    ).addOnCompleteListener(this) {task->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "Sign Up Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            generateUniqueUsernameRDB(firebaseDatabase,{},{
                                Toast.makeText(
                                    this,
                                    "Error->${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()})
                            val user=User()
                            user.username= username
                            firebaseDatabase.child("users").child(firebaseAuth.currentUser!!.uid.toString()).setValue(user)

                            finish()
                        }
                        else {
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
        signinredirect.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
    }
}
data class User(var username:String,var istestdone:Boolean,var level:Int,var dailystreak:Int){
    constructor():this("",false,1,1)
}
fun generateRandomUsername(): String {
    val adjectives = listOf("Calm", "Happy", "Bright", "Quiet", "Kind", "Witty")
    val animals = listOf("Panda", "Tiger", "Koala", "Fox", "Eagle", "Otter")
    val number = (100..999).random()

    return "${adjectives.random()}${animals.random()}$number"
}
fun generateUniqueUsernameRDB(
    database: DatabaseReference,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    fun tryGenerate() {
        username = generateRandomUsername()
        database.child("usernames").child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        // Username is unique, reserve it
                        database.child("usernames").child(username).setValue(true)
                            .addOnSuccessListener { onSuccess(username) }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        tryGenerate() // Retry if not unique
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.toException())
                }
            })
    }

    tryGenerate()
}