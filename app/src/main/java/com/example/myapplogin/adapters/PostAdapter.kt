package com.example.myapplogin.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.R
import com.example.myapplogin.models.Post
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val post = mList[position]
        val user = post.user

        Log.e("Post Adapter", "userId: ${user.uid}")
        Log.e("Post Adapter", "authorId ${post.authorUid}")
        if (post.authorUid == user.uid) {
            Picasso.get().load(user.image).into(holder.authorProfileImage)
        } else {
            Log.e("Post Adapter", " not a match at : $position")
        }

//        holder.usernameTextView.text = post.authorUid
        holder.captionTextView.text = post.caption
        holder.usernameTextView.text = user.username
        Picasso.get().load(post.imageUrl).into(holder.postImageView)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val postImageView: ImageView = itemView.findViewById(R.id.post_image_home)
        val usernameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        val captionTextView: TextView = itemView.findViewById(R.id.description)
        val authorProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)

    }
}
