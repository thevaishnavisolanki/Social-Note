package com.example.myapplogin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.fragment.ProfileFragment
import com.example.myapplogin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,

    ) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userUserNameTextView.text = user.username
        holder.userNameTextView.text = user.name
        Picasso.get().load(user.image).placeholder(R.drawable.ic_avatar).into(holder.userprofileImage)

        checkFollowingstatus(user.uid ?: "",holder.followButton)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileID",user.uid)
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,ProfileFragment()).commit()
        })

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString().equals("follow",true)) {
                firebaseUser?.uid?.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1)
                        .child("Following").child(user.uid ?: "")
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid?.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid ?: "")
                                        .child("Followers").child(it1)
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }


                                }
                            }

                        }

                }

            } else {
                firebaseUser?.uid?.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1)
                        .child("Following").child(user.uid ?: "")
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid?.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid ?: "")
                                        .child("Followers").child(it1)
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }


                                }
                            }

                        }

                }

            }
        }

        holder.itemView.setOnClickListener{
        }
    }


    override fun getItemCount(): Int {
        return mUser.size
    }

    inner class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView = itemView.findViewById(R.id.name_search)
        var userUserNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var userprofileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followButton: TextView = itemView.findViewById(R.id.follow_btn_search)


    }

    private fun checkFollowingstatus(uid: String, followButton: TextView)
    {
        val followingRef = firebaseUser?.uid?.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1)
                .child("Following")
        }
        followingRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(uid).exists()) {
                    followButton.text = "Following"
                } else {
                    followButton.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })

    }

}
