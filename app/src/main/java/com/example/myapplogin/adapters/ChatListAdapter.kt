package com.example.myapplogin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.ChatActivity
import com.example.myapplogin.R
import com.example.myapplogin.TimestampUtils.convertTimestampToDay
import com.example.myapplogin.models.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(
    private var mContext: Context,
    private var conversationList: MutableList<Conversation>
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chatusernameTextView: TextView = itemView.findViewById(R.id.userName_chat)
        var lastchatTextView: TextView = itemView.findViewById(R.id.lastchat)
        var userchatprofileImage: CircleImageView = itemView.findViewById(R.id.chat_profile)
        var messageTimeTextView: TextView = itemView.findViewById(R.id.messageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_chat_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversationList[position]
        val user = conversation.user
        holder.chatusernameTextView.text = user?.username ?: ""

        val timestamp = conversation.updateAt
        val formattedDate = convertTimestampToDay(timestamp as Long)
        holder.messageTimeTextView.text = formattedDate

        holder.lastchatTextView.text = conversation.lastmessage?.message
        Picasso.get().load(user?.image).placeholder(R.drawable.ic_avatar).into(holder.userchatprofileImage)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("conversation", conversation)
            intent.putExtra("selectedUser", conversation.user)
            context.startActivity(intent)
        }
    }
}
