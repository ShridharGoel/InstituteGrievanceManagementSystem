package com.shridhar.institutegrievancemanagementapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.auth.AuthActivity
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils
import com.shridhar.institutegrievancemanagementapp.models.User

class ProfileFragment : Fragment() {

    var imageUri: Uri? = null

    private lateinit var userImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImage = view.findViewById(R.id.userImage)
        val userName: EditText = view.findViewById(R.id.userName)
        val userBio: EditText = view.findViewById(R.id.userBio)
        val saveButton: Button = view.findViewById(R.id.saveButton)
        val logoutButton: Button = view.findViewById(R.id.logoutButton)

        userName.setText(UserUtils.user?.name)
        userBio.setText(UserUtils.user?.bio)

        userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        Glide.with(context!!)
            .load(UserUtils.user?.imageUrl)
            .placeholder(R.drawable.person_icon)
            .centerCrop()
            .into(userImage)

        saveButton.setOnClickListener {
            val newUserName = userName.text.toString()
            val newBio = userBio.text.toString()

            val userDocument = FirebaseFirestore.getInstance().collection("Users")
                .document(UserUtils.user?.uid!!)

            val user = User(
                UserUtils.user?.uid!!,
                newUserName, UserUtils.user?.email!!,
                UserUtils.user?.following!!, UserUtils.user?.followers!!,
                newBio, UserUtils.user?.imageUrl!!)

            userDocument.set(user).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(context, "Details updated.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Please try again.", Toast.LENGTH_LONG).show()
                }
            }

            UserUtils.getCurrentUser()
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            context?.startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            userImage.setImageURI(fileUri)
            imageUri = fileUri
            addUserImage()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserImage() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnCompleteListener {
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

                        val newUser = User(
                            UserUtils.user?.uid!!,
                            UserUtils.user?.name!!, UserUtils.user?.email!!,
                            UserUtils.user?.following!!, UserUtils.user?.followers!!,
                            UserUtils.user?.bio!!, downloadUri.toString())

                        firestore.collection("Users").document(UserUtils.user?.uid!!).set(newUser)
                            .addOnCompleteListener { posted ->
                                if(posted.isSuccessful) {
                                    UserUtils.getCurrentUser()
                                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Error occurred. Please try again.", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Log.d(CreatePostActivity.TAG, urlTaskCompleted.exception.toString())
                    }
                }

            }
    }
}