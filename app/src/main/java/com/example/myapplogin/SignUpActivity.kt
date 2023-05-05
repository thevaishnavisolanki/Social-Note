package com.example.myapplogin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplogin.databinding.ActivitySignUpBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {



    private lateinit var btnRegister: MaterialButton

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        btnRegister = findViewById<MaterialButton>(R.id.btn_sign_in_register)
        btnRegister.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            CreatAccount()
        }
    }

    private fun CreatAccount() {
        val name = findViewById<EditText>(R.id.name_signup).text.toString()
        val username = findViewById<EditText>(R.id.username_signup).text.toString()
        val email = findViewById<EditText>(R.id.email_signup).text.toString()
        val password = findViewById<EditText>(R.id.password_signup).text.toString()

        when {
            TextUtils.isEmpty(name) -> Toast.makeText(this, "name is required.", Toast.LENGTH_SHORT)
                .show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "username is required.",
                Toast.LENGTH_SHORT
            ).show()
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

                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("LogIn")
                progressDialog.setMessage("Please wait while we create your account...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInFo(name, username, email , progressDialog)
                        } else {

                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    }

            }
        }
    }

    private fun saveUserInFo(name: String, username: String, email: String ,progressDialog :ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["name"] = name.toLowerCase()
        userMap["email"] = email
        userMap["username"] = username.toLowerCase()
        userMap["bio"] = ""
        userMap["image"] = "gs://social-note-app.appspot.com/Default Images/profile_icon.png"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignUpActivity,HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val errorMessage = task.exception?.message
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT)
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()


                }
            }
    }
}

