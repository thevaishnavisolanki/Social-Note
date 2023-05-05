package com.example.myapplogin.models

data class Conversation (
    var createdOn: Any? = null,//timestamp
    var creater: String? = null,//userId
    var updateAt: Any? = null,//timestamp
    var conversationID: String = "",//timestamp
    var lastmessage: Message? = null,//last message of conversation
    var members:List<String>?= null//members array with uid
)
