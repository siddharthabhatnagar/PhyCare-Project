package com.project.phycare

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        WebViewManager.init(applicationContext, "https://your-spline-url.com")
        Handler().postDelayed({
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        },3000)
    }
}