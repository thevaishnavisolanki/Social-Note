package com.example.myapplogin

import android.content.Context

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    fun saveUserData(uid: String?, email: String? , username:String? , name:String? , image : String ? , bio:String?) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", uid)
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putString("name", name)
        editor.putString("image", image)
        editor.putString("bio", bio)
        editor.apply()
    }

    fun getUserUid(): String? {
        return sharedPreferences.getString("uid", null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun getUserUsername(): String? {
        return sharedPreferences.getString("username", null)
    }
    fun getUserName(): String? {
        return sharedPreferences.getString("name", null)
    }
    fun getUserImage(): String? {
        return sharedPreferences.getString("image", null)
    }
    fun getUserBio(): String? {
        return sharedPreferences.getString("bio", null)
    }
}
