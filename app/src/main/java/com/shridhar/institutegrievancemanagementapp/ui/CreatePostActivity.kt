package com.shridhar.institutegrievancemanagementapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jaredrummler.materialspinner.MaterialSpinner
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.models.Post
import com.shridhar.institutegrievancemanagementapp.models.User

class CreatePostActivity : AppCompatActivity(), MaterialSpinner.OnItemSelectedListener<String> {
    private var imageUri: Uri? = null

    private lateinit var postImage: ImageView

    private var selectedVisibility = "Public"

    companion object {
        const val TAG = "CreatePostActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postImage = findViewById(R.id.postImage)
        val postText: TextView = findViewById(R.id.postText)

        val postButton: Button = findViewById(R.id.postButton)

        ArrayAdapter.createFromResource(
            this,
            R.array.visibility_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            findViewById<MaterialSpinner>(R.id.selectVisibility).setAdapter(adapter)
        }

        postImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        postButton.setOnClickListener {
            val text = postText.text.toString()

            if(TextUtils.isEmpty(text)) {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            addPost(text)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            postImage.setImageURI(fileUri)
            imageUri = fileUri
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPost(text: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnCompleteListener {
                val user = it.result?.toObject(User::class.java)!!

                val storage = FirebaseStorage.getInstance().reference.child("Images")
                    .child(FirebaseAuth.getInstance().currentUser?.email.toString() + "_" + System.currentTimeMillis() + ".jpg")

                val uploadTask = storage.putFile(imageUri!!)

                uploadTask.continueWithTask { task ->
                    if(!task.isSuccessful) {
                        Log.d("Upload Task", task.exception.toString())
                    }
                    storage.downloadUrl
                }.addOnCompleteListener { urlTaskCompleted ->
                    if(urlTaskCompleted.isSuccessful) {
                        val downloadUri = urlTaskCompleted.result

                        val post = Post(text, downloadUri.toString(), user,
                                        System.currentTimeMillis(), visibility = selectedVisibility)

                        firestore.collection("Posts").document().set(post)
                            .addOnCompleteListener { posted ->
                                if(posted.isSuccessful) {
                                    Toast.makeText(this, "Posted", Toast.LENGTH_LONG).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Log.d(TAG, urlTaskCompleted.exception.toString())
                    }
                }

            }
    }

    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String) {
        selectedVisibility = item
    }
}