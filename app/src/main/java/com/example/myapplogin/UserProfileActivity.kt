package com.example.myapplogin
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplogin.models.User

class UserProfileActivity : AppCompatActivity() {

    private var selectedUser: User?=null
    private lateinit var textViewFullName: TextView
    private lateinit var textViewUsername: TextView
    private lateinit var textViewBio: TextView
    private lateinit var imageView: ImageView
    private lateinit var textViewFollowers: TextView
    private lateinit var textviewFollowing: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        imageView = findViewById(R.id.pro_image_profile_frag)
        textViewUsername = findViewById(R.id.profile_fragment_username)
        textViewFullName = findViewById(R.id.full_name_profile_frag)
        textViewBio = findViewById(R.id.bio_profile_frag)
        textViewFollowers = findViewById(R.id.total_followers)
        textviewFollowing = findViewById(R.id.total_following)


        selectedUser = intent.getSerializableExtra("selectedUser") as? User




    }
}
