package com.example.kotlinchatapp

class ChatMessage(val id : String ,val text : String , val fromId : String , val toId:String , val timestanp:Long){
    constructor():this("","","","",0L)
}