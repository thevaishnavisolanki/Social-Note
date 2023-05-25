package com.example.myapplogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplogin.adapters.ChatListAdapter
import com.example.myapplogin.databinding.ActivityChatListBinding
import com.example.myapplogin.models.Conversation
import com.example.myapplogin.models.User
import com.google.firebase.database.*

class ChatListActivity : AppCompatActivity() {

    private val TAG: String = this.javaClass.simpleName
    private lateinit var binding: ActivityChatListBinding
    private lateinit var adapter: ChatListAdapter
    private lateinit var user_Id: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var conversationRef: DatabaseReference

    private lateinit var userRefListener: ValueEventListener
    private lateinit var conversationRefListener: ValueEventListener

    private var conversationList = mutableListOf<Conversation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatListAdapter(this, conversationList)
        conversationList.clear()
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChat.adapter = adapter


        user_Id = SharedPreferencesManager(context = applicationContext).getUserUid()!!

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)


        }

        databaseRef = FirebaseDatabase.getInstance().reference
        userRef = databaseRef.child("users/")
        conversationRef = userRef.child(user_Id).child("conversations/")


    }

    override fun onResume() {
        super.onResume()
        conversationList.clear()
        conversationRefListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    conversationList.clear() // Clear the list before adding conversations
                    for (data in snapshot.children) {
                        Log.d(TAG, "onDataChange: snapshot: $data")
                        Log.d(TAG, "onDataChange: snapshot: $user_Id")
                        if (data != null) {
                            val conversation = data.getValue(Conversation::class.java)

                            if (conversation != null) {
                                val users = conversation.members?.filter { it != user_Id }
                                Log.d(TAG, "onDataChange: snapshot: $users")

                                users?.let {
                                    userRefListener = object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val user = snapshot.getValue(User::class.java)
                                                conversation.user = user
                                                conversationList.add(conversation)

                                                Log.d(
                                                    TAG,
                                                    "onDataChange: conversationList: $conversationList"
                                                )
                                                adapter.notifyDataSetChanged()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    }
                                    userRef.child(it.first())
                                        .addListenerForSingleValueEvent(userRefListener)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        conversationRef.addValueEventListener(conversationRefListener)
    }
    override fun onDestroy() {
        userRef.removeEventListener(userRefListener)
        conversationRef.removeEventListener(conversationRefListener)
        super.onDestroy()
    }
}