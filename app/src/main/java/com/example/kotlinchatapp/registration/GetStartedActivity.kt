package com.example.kotlinchatapp.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth

class GetStartedActivity : AppCompatActivity() {
    private lateinit var getStartedButton : Button
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        auth = FirebaseAuth.getInstance()

        getStartedButton = findViewById(R.id.getstartedbutton)
        getStartedButton.setOnClickListener {
            redirectToActivity("LOGIN")
        }

        if(auth.currentUser!=null){
            Toast.makeText(this,"User is already logged in !!" , Toast.LENGTH_SHORT).show()
            redirectToActivity("LATESTMESSAGES")
        }
    }

    private fun redirectToActivity(name:String){
        val intent = when(name){
            "LOGIN"-> Intent(this@GetStartedActivity , LoginActivity::class.java)
            "LATESTMESSAGES" -> Intent(this@GetStartedActivity , LatestMessagesActivity::class.java)
            else-> throw Exception("no path exists")
        }
        startActivity(intent)
        finish()
    }
}