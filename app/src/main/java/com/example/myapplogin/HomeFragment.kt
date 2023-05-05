package com.example.myapplogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.adapters.PostAdapter
import com.example.myapplogin.models.Post
import com.example.myapplogin.models.User
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*


class HomeFragment : Fragment() {


    private lateinit var imageView: ImageView
    var postUserList:ArrayList<User> = ArrayList()
    var postList = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false);
        val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view_home)
        imageView = view.findViewById(R.id.chat_view)

        imageView.setOnClickListener{
            val intent = Intent(context, ChatListActivity::class.java)
            startActivity(intent)
        }





        recyclerview.layoutManager = LinearLayoutManager(requireContext())
            .apply {
            reverseLayout = true
            stackFromEnd = true
        }


        val reference = FirebaseDatabase.getInstance().getReference("posts")
        val query = reference.orderByChild("timestamp")

        val options = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()




        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear()
                    for (posts in dataSnapshot.children) {
                        val snapshot: DataSnapshot = posts
                        val imageUrl: String? =
                            snapshot.child("imageUrl").getValue(String::class.java)
                        val caption: String? =
                            snapshot.child("caption").getValue(String::class.java)
                        val authorUid: String? =
                            snapshot.child("authorUid").getValue(String::class.java)
                        val timestamp: Long? =
                            snapshot.child("timestamp").getValue(Long::class.java)


                        Log.d("onDataChange", "imageUrl: ${imageUrl}")
                        Log.d("onDataChange", "caption: ${caption}")
                        Log.d("onDataChange", "authorUid: ${authorUid}")
                        Log.d("onDataChange", "timestamp: ${timestamp}")

                        postList.add(
                            Post(
                                imageUrl ?: "",
                                caption ?: "",
                                authorUid ?: "",
                                timestamp ?: "",
                                User()
                            )
                        )
                    }
                    if (postUserList.isNotEmpty()) {
                        postUserList.clear()

                    }
                    postList.forEach { post ->
                        val usersRef: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("users")
                        val queryUser = usersRef.orderByChild("uid")
                            .equalTo(post.authorUid)



                        queryUser.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                if (snapshot1.exists()) {
                                    val postUserList = ArrayList<User>()
                                    for (snapshotUser in snapshot1.children) {
                                        val user = snapshotUser.getValue(User::class.java)
                                        if (user != null) {
                                            postUserList.add(user)
                                        }
                                    }

                                    for ((i, post_) in postList.withIndex()) {
                                        for ((j, user_) in postUserList.withIndex()) {
                                            if (post_.authorUid == user_.uid) {
                                                postList[i].user = user_
                                                break
                                            }
                                        }
                                    }
                                    val adapter = PostAdapter(postList)
                                    recyclerview?.adapter = adapter
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle onCancelled event here
                            }
                        })



                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        return view
    }

    companion object {

    }


}
