package com.example.myapplogin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.R
import com.example.myapplogin.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MessageAdapter(
    private var mContext: Context,
    private var messageList: MutableList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun getItemViewType(position: Int): Int {
        return if (messageList.get(position).sentBy?.equals(firebaseUser?.uid) == true) {
            VIEW_TYPE_ONE
        } else VIEW_TYPE_TWO
    }

    private inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvmessage: TextView = itemView.findViewById<TextView>(R.id.tv_message)

        fun bind(message: Message, position: Int) {
            tvmessage.text = message.message
        }
    }

    private inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvmessage: TextView = itemView.findViewById<TextView>(R.id.tv_message)

        fun bind(message: Message, position: Int) {
            tvmessage.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            return SenderViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.send_msg, parent, false)
            )
        }
        return ReceiverViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.receive_msg, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "onBindViewHolder: firebaseUser: ${firebaseUser?.uid}")
        setData(holder, position)
        holder.setIsRecyclable(false)
    }

    private fun setData(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SenderViewHolder) {
            holder.bind(message, position)
        } else {
            (holder as ReceiverViewHolder).bind(message, position)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}
