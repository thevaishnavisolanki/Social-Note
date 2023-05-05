package com.example.myapplogin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myapplogin.models.Post
import com.example.myapplogin.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class AddPostActivity : AppCompatActivity() {
    private val TAG: String=this.javaClass.toString()
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: TextView
    private lateinit var captionEditText: EditText
    private lateinit var postButton: MaterialButton
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        // Find views by their IDs
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)
        imageView = findViewById(R.id.imageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        captionEditText = findViewById(R.id.captionEditText)
        postButton = findViewById(R.id.postButton)

        // Set toolbar as the support action bar
        setSupportActionBar(toolbar)

        // Set the title of the toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.text = getString(R.string.title_add)

        // Set a click listener for the select image button
        selectImageButton.setOnClickListener {
            // Launch the device's gallery app to select an image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Set a click listener for the post button
        postButton.setOnClickListener {

            // Get the caption text
            val caption = captionEditText.text.toString().trim()

            // Post the image with the caption
            postImage(imageUri, caption)
        }
    }

    // Handle the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            imageUri = data.data

            // Set the selected image to the ImageView
            imageView.setImageURI(imageUri)
        }
    }

    // Post the image with the caption
    private fun postImage(imageUri: Uri?, caption: String) {
        // Show a progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Posting...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("posts").child("${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(imageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "postImage: something went wrong")
                throw task.exception!!
            }
            // Continue with the task to get the download URL
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the download URL
                val downloadUrl = task.result.toString()
                Log.d(TAG, "postImage| downloadUrl: $downloadUrl")

                // Create a Post object with the download URL and caption
                val post = Post(
                    imageUrl = downloadUrl,
                    caption = caption,
                    authorUid = FirebaseAuth.getInstance().currentUser!!.uid,
                    timestamp = ServerValue.TIMESTAMP,
                    user = User()
                )

                // Save the Post object to Firebase Database
                val postRef = FirebaseDatabase.getInstance().reference.child("posts").push()
                postRef.setValue(post).addOnCompleteListener { postTask ->
                    if (postTask.isSuccessful) {
                        // Get the key of the newly created post
                        val postId = postRef.key

                        // Save the post to the "user-posts" node under the current user's UID
                        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
                        val userPostsRef = FirebaseDatabase.getInstance().reference.child("user-posts").child(currentUserUid).child(postId!!)
                        userPostsRef.setValue(true)

                        // Add the post to the feed of all followers of the current user
                        val followersRef = FirebaseDatabase.getInstance().reference.child("followers").child(currentUserUid)
                        followersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (followerSnapshot in dataSnapshot.children) {
                                    val followerUid = followerSnapshot.key!!
                                    val feedRef = FirebaseDatabase.getInstance().reference.child("feed").child(followerUid).child(postId)
                                    feedRef.setValue(true)
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle error
                            }
                        })

                        // Dismiss the progress dialog
                        progressDialog.dismiss()

                        // Finish the activity
                        finish()
                    } else {
                        // Handle error
                    }
                }
            } else {
                // Handle error
            }
        }
    }


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }


}
