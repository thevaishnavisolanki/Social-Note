package com.example.myapplogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.adapters.MessageAdapter
import com.example.myapplogin.models.Conversation
import com.example.myapplogin.models.Message
import com.example.myapplogin.models.User
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageRef: DatabaseReference
    private lateinit var conversationRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private var conversation: Conversation?=null
    private var selectedUser: User?=null
    private lateinit var sendBtn: ImageView
    private val TAG = "ChatActivity"
    private lateinit var user_Id: String
    private lateinit var uid: String
    private lateinit var messageBox: EditText
    private var messageList = mutableListOf<Message>()
    private lateinit var backBtn: ImageView
    private lateinit var toolbar :Toolbar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        selectedUser = intent.getSerializableExtra("selectedUser") as? User
        conversation = intent.getSerializableExtra("conversation") as? Conversation
            toolbar=findViewById(R.id.toolbar)
            toolbar.setOnClickListener {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("selectedUser",selectedUser )
                startActivity(intent)
            }
        user_Id = SharedPreferencesManager(context = applicationContext).getUserUid()!!
        Log.d(TAG, "onCreate: user_Id: $user_Id")




        Log.d(TAG, "onCreate: conversation:$conversation")

        //database reference
        databaseRef = FirebaseDatabase.getInstance().reference
        messageRef = databaseRef.child("messages/")
        conversationRef = databaseRef.child("conversations/")
        userRef = databaseRef.child("users/")

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
        backBtn=findViewById(R.id.imageView2)
        recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        messageAdapter=MessageAdapter(this,messageList)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=messageAdapter


        recyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView.post {
                    val lastAdapterItem = messageAdapter.itemCount - 1
                    var recyclerViewPositionOffset = -1000000
                    val bottomView = recyclerView.layoutManager?.findViewByPosition(lastAdapterItem)
                    if (bottomView != null) {
                        recyclerViewPositionOffset = 0 - bottomView.height
                    }
                    (recyclerView.layoutManager as LinearLayoutManager)?.scrollToPositionWithOffset(lastAdapterItem, recyclerViewPositionOffset)
                }
            }
        }


        Log.d(TAG, "onDataChange: message: ${ conversation?.conversationID}")

        if (conversation!=null) getConversations()

        backBtn.setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }


        sendBtn.setOnClickListener {

            Toast.makeText(this,"click event",Toast.LENGTH_SHORT).show()
            val messageText = messageBox.text.toString().trim()

            if (messageText.isNotEmpty()) {
                Log.d(TAG, "Message create: message text: $messageText")

                val conversationID=if (conversation==null) "${user_Id}_$uid" else conversation!!.conversationID

                val message = Message(
                    type = "text",
                    message = messageText,
                    createdAt = ServerValue.TIMESTAMP,
                    updatedAt = ServerValue.TIMESTAMP,
                    receiverId = uid,
                    sentBy = user_Id,
                    conversationId = conversationID
                )
                Log.d(TAG, "Message create: message: $message")

                if (messageList.isEmpty()) {

                    val members = listOf(user_Id, uid)
                    conversation = Conversation(
                        creater = user_Id,
                        createdOn = ServerValue.TIMESTAMP,
                        updateAt = ServerValue.TIMESTAMP,
                        lastmessage = message,
                        members = members,
                        conversationID = message.conversationId
                    )
                    getConversations()
                    conversation?.conversationID?.let { it1 ->
                        conversationRef.child(it1).setValue(conversation)
                            .addOnCompleteListener { conversationTask ->
                                if (conversationTask.isSuccessful) {
                                    conversation?.conversationID?.let { it1 ->
                                        userRef.child(user_Id).child("conversations").child(
                                            it1
                                        ).setValue(conversation)
                                        userRef.child(uid).child("conversations").child(it1).setValue(conversation)
                                    }
                                    sendMessage(message,false)
                                } else {

                                }
                            }
                    }
                }
                else {
                    sendMessage(message, true)
                }

            }
        }
    }


    private fun getConversations() {
        conversation?.conversationID?.let { messageRef.orderByChild("conversationId").equalTo(it).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    messageList.clear()
                    for (data in snapshot.children){
                        Log.d(TAG, "onDataChange: message: $data")
                        val message=data.getValue(Message::class.java)
                        message?.let { it1 -> messageList.add(it1) }
                    }
                    recyclerView.scrollToPosition(messageList.size - 1)

                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }) }

    }

    private fun sendMessage(message: Message, updateConversation: Boolean) {
        val messageKey=messageRef.push().key
        messageKey?.let {
            if (updateConversation){
                conversation?.lastmessage=message
                conversation?.updateAt=ServerValue.TIMESTAMP

                conversation?.conversationID?.let { it1 ->
                    userRef.child(user_Id).child("conversations").child(it1).setValue(conversation)
                    userRef.child(uid).child("conversations").child(it1).setValue(conversation)
                    conversationRef.child(it1).setValue(conversation)
                }
            }
            messageRef.child(it).setValue(message.copy(messageId =it,
            conversationId = conversation?.conversationID?:""))
            messageBox.text.clear()

        }
    }


}


