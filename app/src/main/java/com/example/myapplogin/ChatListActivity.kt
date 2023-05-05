package com.example.myapplogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplogin.databinding.ActivityChatListBinding

class ChatListActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChatListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.floatingActionButton.setOnClickListener {
                val intent = Intent(this, UserListActivity::class.java)
                startActivity(intent)


            // Create a conversation object if user's not started conversation before
//            val conversation = Conversation(
//                creater = null,
//                createdOn = ServerValue.TIMESTAMP,
//                updateAt = ServerValue.TIMESTAMP,
//                lastmessage=null,
//                members=null
//            )

            // Save the Conversation object to Firebase Database



//            val conversationRef = FirebaseDatabase.getInstance().reference.child("conversations").push()
//            conversationRef.setValue(conversation).addOnCompleteListener { conversationTask ->
//                if (conversationTask.isSuccessful) {
//
//                    // Get the key of the newly created conversation
//                    val conversationId = conversationRef.key
//
//                    // Save the conversation to the "user-conversations" node under the current user's UID
//                    val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
//                    val userConversationsRef = FirebaseDatabase.getInstance().reference.child("user-conversations").child(currentUserUid).child(conversationId!!)
//                    userConversationsRef.setValue(true)
//
//                    // Add the conversation to the list of all members' conversations
//                    val members = conversation.members
//                    if (members != null) {
//                        for (memberId in members) {
//                            val memberConversationsRef = FirebaseDatabase.getInstance().reference.child("user-conversations").child(
//                                memberId.toString()
//                            ).child(conversationId)
//                            memberConversationsRef.setValue(true)
//                        }
//                    }
//
//                    finish()
//                } else {
//                    // Handle error
//                }
//            }



        }
    }
}