package com.example.kotlinchatapp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinchatapp.ChatMessage
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatLogActivity : AppCompatActivity() {
    private lateinit var recyclerview : RecyclerView
    private lateinit var send_button : Button
    private lateinit var sendMessageEditText : EditText
    companion object{
        val TAG = "ChatLog"
    }
    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var db : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        db = FirebaseDatabase.getInstance()

        val user = intent.getParcelableExtra<User>(UsersActivity.USERNAME_KEY)
        supportActionBar?.title = user!!.username

        recyclerview = findViewById(R.id.recyclerview_chat_log)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        send_button = findViewById(R.id.sendmessage)
        send_button.setOnClickListener {
            sendMessage()
        }
        listenForMessages()
    }
    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(UsersActivity.USERNAME_KEY)
        val toId = user!!.uid
        val ref = db.getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage!=null){
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text , currentUser!!))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text , user))
                    }
                }
                recyclerview.scrollToPosition(adapter.itemCount-1)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendMessage(){
        sendMessageEditText = findViewById(R.id.message_chat_log)
        val text = sendMessageEditText.text.toString()

        if(text!="") {
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<User>(UsersActivity.USERNAME_KEY)
            val toId = user!!.uid

            val ref = db.getReference("/user-messages/$fromId/$toId").push()
            val chatMessage = ChatMessage(ref.key!!, text, fromId!!, toId, System.currentTimeMillis() / 1000)

            val toRef = db.getReference("/user-messages/$toId/$fromId").push()

            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    sendMessageEditText.text.clear()
                    recyclerview.scrollToPosition(adapter.itemCount - 1)
                }
            toRef.setValue(chatMessage)

            val latestMessageFromRef = db.getReference("/latest-messages/$fromId/$toId")
            latestMessageFromRef.setValue(chatMessage)

            val latestMessageToRef = db.getReference("/latest-messages/$toId/$fromId")
            latestMessageToRef.setValue(chatMessage)
        } else{
            Toast.makeText(this , "please enter the message",Toast.LENGTH_SHORT).show()
        }
    }
}

class ChatFromItem(val text:String , val user:User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val text_from : TextView = viewHolder.itemView.findViewById(R.id.usermessage_chat_from)
        val image_from : ImageView = viewHolder.itemView.findViewById(R.id.userimage_chat_from)

        text_from.text = text
        Picasso.get().load(user.profileUrl).placeholder(R.drawable.ic_launcher_background).into(image_from)
    }
}
class ChatToItem(val text:String , val user:User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val text_to : TextView = viewHolder.itemView.findViewById(R.id.usermessage_chat_to)
        val image_to : ImageView = viewHolder.itemView.findViewById(R.id.userimage_chat_to)

        text_to.text = text
        Picasso.get().load(user.profileUrl).placeholder(R.drawable.ic_launcher_background).into(image_to)
    }
}