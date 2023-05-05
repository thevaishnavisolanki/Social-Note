package com.example.myapplogin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplogin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap


class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var logoutbutton: Button
    private lateinit var editimagebutton: Button
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var doneButton: Button
    private lateinit var profileID: String
    private lateinit var closeImageView: ImageView
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null


    private val REQUEST_CODE_GALLERY_PERMISSIONS = 1245

    override fun closeContextMenu() {
        val IMAGE_REGUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        imageView = findViewById(R.id.ivProfile)
        logoutbutton = findViewById(R.id.logout_btn)
        editimagebutton = findViewById(R.id.change_image_text_btn)
        nameEditText = findViewById(R.id.full_name_profile_frag)
        usernameEditText = findViewById(R.id.username_profile_frag)
        bioEditText = findViewById(R.id.bio_profile_frag)
        doneButton = findViewById(R.id.save_infor_profile_btn)
        closeImageView = findViewById(R.id.close_button)


        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val pref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileID = pref.getString("profileID", "none").toString()
        }

        userInfo()

        doneButton.setOnClickListener {
            setUserInfo()

        }
        closeImageView.setOnClickListener {
            onBackPressed()
        }






        editimagebutton.setOnClickListener {
            openGallery()

        }

        logoutbutton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }


        }


        // Request permission to read external storage
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_GALLERY_PERMISSIONS
            )
        } else {
            openGallery()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_GALLERY_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY_PERMISSIONS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALLERY_PERMISSIONS && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                val uri = data.data     //filePath
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val filePath = uri.toString()
                            val obj = HashMap<String, Any>()
                            obj["image"] = filePath
                            database!!.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnSuccessListener { }
                        }
                    }
                }
                imageView.setImageURI(uri)
                selectedImage = data.data
                Log.i("SelectedImage", selectedImage.toString())
            }
        }

    }

    private fun userInfo() {
        FirebaseDatabase.getInstance().reference.child("users").child(profileID).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.getValue(User::class.java)
                    if (!user!!.image.isNullOrBlank()) {
                        Picasso.get().load(user.image).placeholder(R.drawable.ic_avatar)
                            .into(imageView)
                    }
                    nameEditText.setText(user.name)
                    usernameEditText.setText(user.username)
                    bioEditText.setText(user.bio)

                }
            }
    }

    private fun setUserInfo() {
        val firebaseAuth = FirebaseAuth.getInstance().currentUser!!
        val uid = firebaseAuth.uid
        var image: String?
        if (selectedImage != null) {
            val reference = storage!!.reference.child("Profile").child(uid)
            reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    reference.downloadUrl.addOnCompleteListener { uri ->
                        image = uri.result.toString()
                        val currentUser = User(
                            name = nameEditText.text.toString(),
                            username = usernameEditText.text.toString(),
                            bio = bioEditText.text.toString(),
                            image = image,
                            email = firebaseAuth.email,
                            uid = uid
                        )
                        Log.i("selectedImage", image!!)
                        FirebaseDatabase.getInstance().reference.child("users").child(profileID)
                            .setValue(currentUser).addOnSuccessListener {
                                finish()
                            }
                    }
                } else {
                    val currentUser = User(
                        name = nameEditText.text.toString(),
                        username = usernameEditText.text.toString(),
                        bio = bioEditText.text.toString(),
                        image = "No Img",
                        email = firebaseAuth.email,
                        uid = uid
                    )
                    FirebaseDatabase.getInstance().reference.child("users").child(profileID)
                        .setValue(currentUser).addOnSuccessListener {
                            finish()
                        }
                }

            }
        }
    }

}
