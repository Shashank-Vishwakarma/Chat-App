package com.example.kotlinchatapp.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton : Button
    private lateinit var registerTextView : TextView
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //get the ids
        emailEditText = findViewById(R.id.login_email_edittext)
        passwordEditText = findViewById(R.id.login_password_edittext)
        loginButton = findViewById(R.id.login_button)
        registerTextView = findViewById(R.id.register_textview)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            loginUser()
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity , RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(email.isBlank() || password.isBlank()){
            Toast.makeText(this,"Please , fill in the login info" , Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    val intent = Intent(this@LoginActivity , LatestMessagesActivity::class.java)
                    startActivity(intent)
                    finish()
                } else{
                    Toast.makeText(this,"Oops , Login failed!!"+it.exception.toString() , Toast.LENGTH_SHORT).show()
                }
            }
    }
}