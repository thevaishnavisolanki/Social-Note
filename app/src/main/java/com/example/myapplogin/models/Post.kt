package com.example.myapplogin.models

data class Post(
    val imageUrl: String,
    val caption: String,
    val authorUid: String,
    val timestamp: Any,
    var user:User
)
