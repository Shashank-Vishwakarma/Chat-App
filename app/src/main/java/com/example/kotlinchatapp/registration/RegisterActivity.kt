package com.example.kotlinchatapp.registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.User
import com.example.kotlinchatapp.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerButton : Button
    private lateinit var loginTextView : TextView
    private lateinit var auth : FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var selectPhotoButton : Button
    private var selectedPhotoUri : Uri? = null
    private lateinit var circleimageview : CircleImageView
    private lateinit var usernameedittext : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // get the ids
        emailEditText = findViewById(R.id.register_email_edittext)
        passwordEditText = findViewById(R.id.register_password_edittext)
        registerButton = findViewById(R.id.register_button)
        loginTextView = findViewById(R.id.login_textview)
        selectPhotoButton = findViewById(R.id.select_photo_register)
        circleimageview = findViewById(R.id.circleImageView)
        usernameedittext = findViewById(R.id.register_username_edittext)

        // initializing the firebase authentication
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            registerUser()
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            circleimageview.setImageBitmap(bitmap)
            selectPhotoButton.alpha = 0f
        }
    }

    private fun registerUser(){
        val username = usernameedittext.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(username.isBlank() || email.isBlank() || password.isBlank()){
            Toast.makeText(this,"Please , fill in the registration info" , Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    uploadUserImageToFirebaseStorage()

                    // send the user to Latest messages Dashboard
                    val intent = Intent(this@RegisterActivity , LatestMessagesActivity::class.java)
                    startActivity(intent)
                    finish()
                } else{
                    Toast.makeText(this,"Oops , Registration failed!!\n" + it.exception.toString() , Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadUserImageToFirebaseStorage(){
        if(selectedPhotoUri!=null){
            val firebaseStorage = FirebaseStorage.getInstance()
            val filename = UUID.randomUUID().toString()
            val ref = firebaseStorage.getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    saveUserToFirebaseDatabase(selectedPhotoUri.toString())
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity","upload failed")
                }
        }
    }

    private fun saveUserToFirebaseDatabase(profileImage:String){
        val database = FirebaseDatabase.getInstance()

        val uid = FirebaseAuth.getInstance().uid
        val ref = database.getReference("/users/$uid")

        val username = usernameedittext.text.toString()
        val user = User(uid!!,profileImage , username)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","user saved to database")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","error saving the user")
          }
    }
}