package com.example.myapplogin


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplogin.adapters.UserchatlistAdapter
import com.example.myapplogin.databinding.ActivityUserListBinding
import com.example.myapplogin.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UserchatlistAdapter
    private var userlist: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserchatlistAdapter(this, userlist)
        binding.recyclerViewUser.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUser.adapter = adapter



        Firebase.auth.currentUser?.let { currentUser ->
            val usersRef = FirebaseDatabase.getInstance().reference.child("Follow").child(currentUser.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val userIdSet = HashSet<String>() // to keep track of unique user IDs
                        val users = snapshot.child("Followers").children + snapshot.child("Following").children
                        users.forEach {
                            val userId = it.key.toString()
                            if (!userIdSet.contains(userId)) { // add only if user ID is not already in the set
                                userIdSet.add(userId)
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(userId).get().addOnSuccessListener { userSnapshot ->
                                        val user = userSnapshot.getValue(User::class.java)
                                        user?.let {
                                            userlist.add(user)
                                        }
                                        adapter.notifyDataSetChanged()
                                    }
                            }
                        }
                    }
                }
        }


    }
}


