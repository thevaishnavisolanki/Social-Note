package com.example.myapplogin

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplogin.models.Conversation
import com.example.myapplogin.models.Message
import com.example.myapplogin.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private lateinit var messageRef: DatabaseReference
    private lateinit var sendBtn: ImageView
    private val TAG = "ChatActivity"
    private lateinit var user_Id: String
    private lateinit var uid: String
    private lateinit var messageBox: EditText
    private var messageList = mutableListOf<Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        user_Id = SharedPreferencesManager(context = applicationContext).getUserUid()!!
        Log.d(TAG, "onCreate: user_Id: $user_Id")
        val selectedUser = intent.getSerializableExtra("selectedUser") as? User
        messageRef = FirebaseDatabase.getInstance().reference.child("messages/")

        selectedUser?.let {
            findViewById<TextView>(R.id.name).text = it.username
            uid = it.uid!!
            Log.d(TAG, "onCreate: uid: $uid")
            findViewById<ImageView>(R.id.profile01).let { imageView ->
                Picasso.get().load(it.image).placeholder(R.drawable.ic_avatar).into(imageView)
            }
        }

        messageBox = findViewById<EditText>(R.id.messageBox)
        sendBtn=findViewById<ImageView>(R.id.sendBtn)

//        messageRef.orderByValue().equalTo()

        sendBtn.setOnClickListener {

            Toast.makeText(this,"click event",Toast.LENGTH_SHORT).show()
            val messageText = messageBox.text.toString().trim()

            if (messageText.isNotEmpty()) {
                Log.d(TAG, "Message create: message text: $messageText")
                val databaseRef = FirebaseDatabase.getInstance().reference
                val conversationRef = databaseRef.child("conversations/")

                val message = Message(
                    type = "text",
                    message = messageText,
                    createdAt = ServerValue.TIMESTAMP,
                    updatedAt = ServerValue.TIMESTAMP,
                    receiverId = uid,
                    sentBy = user_Id,
                    conversationId = "${user_Id}_$uid",
                )
                Log.d(TAG, "Message create: message: $message")

                // Create a conversation object if user's not started conversation before
                if (messageList.isEmpty()) {

                    val members = listOf(user_Id, uid)

//                    val conversationId = conversationRef.push().getKey()
                    val conversation = Conversation(
                        creater = user_Id,
                        createdOn = ServerValue.TIMESTAMP,
                        updateAt = ServerValue.TIMESTAMP,
                        lastmessage = message,
                        members = members,
                        conversationID = message.conversationId
                    )
                    conversationRef.child(conversation.conversationID).setValue(conversation)
                        .addOnCompleteListener { conversationTask ->
                            if (conversationTask.isSuccessful) {
                                sendMessage(message)
                            } else {

                            }
                        }
                }
                else
                    sendMessage(message)

            }
        }
    }

    private fun sendMessage(message: Message) {
        val messageKey=messageRef.push().key
        messageKey?.let {messageRef.child(it).setValue(message.copy(messageId =it)) }
    }
}


