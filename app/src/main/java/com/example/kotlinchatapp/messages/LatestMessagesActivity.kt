package com.example.kotlinchatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinchatapp.ChatMessage
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.User
import com.example.kotlinchatapp.registration.LoginActivity
import com.example.kotlinchatapp.registration.RegisterActivity
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessagesActivity : AppCompatActivity() {
    companion object{
        var currentUser : User? = null
    }
    private lateinit var recyclerview : RecyclerView
    val adapter = GroupAdapter<ViewHolder>()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        progressBar = findViewById(R.id.progressBar_latest_messages)

        fetchCurrentUser()
        listenForLatestMessages()

        recyclerview = findViewById(R.id.recyclerview_latest_messages)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as LatestMessagesItem

            val intent = Intent(this , ChatLogActivity::class.java)
            intent.putExtra(UsersActivity.USERNAME_KEY , userItem.chatPartenerUser)
            startActivity(intent)
        }
    }

    val latestMessagesMap = HashMap<String , ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessagesItem(it))
        }
    }

    private fun listenForLatestMessages(){
        progressBar.visibility = View.VISIBLE

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()

                progressBar.visibility = View.GONE
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    class LatestMessagesItem(val chatMessage: ChatMessage) : Item<ViewHolder>(){
        var chatPartenerUser : User? = null
        override fun getLayout(): Int {
            return R.layout.latest_chats
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val usernameTextView = viewHolder.itemView.findViewById<TextView>(R.id.username_latest_messages)
            val latestMessageTextView = viewHolder.itemView.findViewById<TextView>(R.id.latest_message_from_user)
            val userProfileImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.user_image_latest_messages)

            latestMessageTextView.text = chatMessage.text

            val chatPartenerId : String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartenerId = chatMessage.toId
            } else{
                chatPartenerId = chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartenerId")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartenerUser = snapshot.getValue(User::class.java)
                    usernameTextView.text = chatPartenerUser!!.username
                    Picasso.get().load(chatPartenerUser!!.profileUrl).placeholder(R.drawable.ic_launcher_background).into(userProfileImageView)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_messages -> {
                val intent = Intent(this, UsersActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_Sign_Out -> {
                val alert = AlertDialog.Builder(this)
                alert.setMessage("Do you really want to sign out ?")
                alert.setPositiveButton("OK"){ dialog , positiveButton ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                alert.show()
            }
            else-> throw Exception("no path exists")
        }
        return super.onOptionsItemSelected(item)
    }
}