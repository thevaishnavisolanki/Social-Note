package com.example.myapplogin.models

data class Message(
    val type:String?=null,
    val message: String?=null,
    val createdAt: Any?=null,
    val updatedAt: Any?=null,
    val sentBy: String?=null,
    val receiverId: String?=null,
    val conversationId: String="",
    val messageId: String?=null
):java.io.Serializable
