package com.example.kotlinchatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.User
import com.example.kotlinchatapp.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class UsersActivity : AppCompatActivity(){
    private lateinit var recyclerView : RecyclerView

    companion object{
        val USERNAME_KEY : String = "USERNAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        supportActionBar?.title = "Select User"

        recyclerView = findViewById(R.id.recyclerview_users_activity)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsers()
    }

    private fun fetchUsers(){

        val progressBar : ProgressBar = findViewById(R.id.progressbar_users_activity)
        progressBar.visibility = View.VISIBLE

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("/users")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user))
                }
                recyclerView.adapter = adapter
                progressBar.visibility = View.GONE
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(this@UsersActivity,ChatLogActivity::class.java)
                    intent.putExtra(USERNAME_KEY,userItem.user!!)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}