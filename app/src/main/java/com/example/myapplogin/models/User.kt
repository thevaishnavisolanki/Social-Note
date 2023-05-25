package com.example.myapplogin.models

data class User(
    var username : String ?= null,
    var name : String ?= null,
    var bio : String ?= null,
    var image : String ?= null,
    var uid : String ?= null,
    var email:String?=null,

):java.io.Serializable