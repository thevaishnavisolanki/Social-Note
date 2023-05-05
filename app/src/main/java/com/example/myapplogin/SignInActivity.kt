package com.example.myapplogin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var btnLogin: MaterialButton
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitysignin)



        btnLogin = findViewById<MaterialButton>(R.id.btn_sign_in_login)
        btnLogin.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            loginuser()
        }

        textView = findViewById(R.id.tv_Sign_Up)
        textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loginuser() {
        val email = findViewById<EditText>(R.id.email_login).text.toString()
        val password = findViewById<EditText>(R.id.password_login).text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "email is required.",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is required.",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("Please wait while we create your account...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressDialog.dismiss()

                            // Save user data to SharedPreferences
                            val uid = mAuth.currentUser?.uid
                            val username = mAuth.currentUser?.displayName
                            val name = ""
                            val image = mAuth.currentUser?.photoUrl?.toString()
                            val bio = ""
                            val sharedPreferencesManager = SharedPreferencesManager(this)
                            sharedPreferencesManager.saveUserData(uid, email, username, name, image, bio)

                            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
