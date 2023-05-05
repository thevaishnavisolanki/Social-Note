package com.example.myapplogin.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplogin.R
import com.example.myapplogin.UserAdapter
import com.example.myapplogin.models.User
import com.google.firebase.database.*


class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)


        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>) }
        recyclerView?.adapter = userAdapter

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // not implemented
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchEditText.text.toString().isEmpty()) {
                    // clear the list of users and hide the recyclerview if the search bar is empty
                    mUser?.clear()
                    userAdapter?.notifyDataSetChanged()
                    recyclerView?.visibility = View.GONE
                } else {
                    recyclerView?.visibility = View.VISIBLE
                    searchUser(searchEditText.text.toString().toLowerCase())
                }

            }

            override fun afterTextChanged(p0: Editable?) {
                // not implemented
            }
        })

        return view
    }

    private fun searchUser(input: String) {

        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val query = usersRef.orderByChild("username")
            .startAt(input)
            .endAt("$input\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle database errors if any
            }
        })
    }
}
