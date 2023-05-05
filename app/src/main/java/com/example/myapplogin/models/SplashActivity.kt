package com.example.myapplogin.models

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplogin.HomeActivity
import com.example.myapplogin.R
import com.example.myapplogin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


        Handler().postDelayed({
            val intent: Intent = if (mAuth.currentUser != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, SignInActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000) // 3 seconds delay


    }
}
