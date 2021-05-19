package com.shridhar.institutegrievancemanagementapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jaredrummler.materialspinner.MaterialSpinner
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.databinding.FragmentRaiseTicketBinding
import com.shridhar.institutegrievancemanagementapp.models.Ticket
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RaiseTicketFragment : Fragment(), MaterialSpinner.OnItemSelectedListener<String> {

    private lateinit var binding: FragmentRaiseTicketBinding
    private var selectedCategory = "Academic"

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_raise_ticket, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
            context!!,
            R.array.categories_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.categorySelector.setAdapter(adapter)
        }

        binding.categorySelector.setOnItemSelectedListener(this)

        binding.postImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.postButton.setOnClickListener {
            val text = binding.postText.text.toString()

            if (TextUtils.isEmpty(text)) {
                Toast.makeText(activity, "Description cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            binding.postButton.text = "Uploading...."

            addTicket(text)
        }
    }

    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String) {
        selectedCategory = item
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            binding.postImage.setImageURI(fileUri)
            imageUri = fileUri
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTicket(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val firestore = FirebaseFirestore.getInstance()
            val user = UserUtils.user

            val storage = FirebaseStorage.getInstance().reference.child("Images")
                .child(FirebaseAuth.getInstance().currentUser?.email.toString() + "_" + System.currentTimeMillis() + ".jpg")

            val uploadTask = storage.putFile(imageUri!!)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("Upload Task", task.exception.toString())
                }
                storage.downloadUrl
            }.addOnCompleteListener { urlTaskCompleted ->
                if (urlTaskCompleted.isSuccessful) {
                    val downloadUri = urlTaskCompleted.result

                    val post = user?.let {
                        Ticket(
                            text, downloadUri.toString(), it,
                            System.currentTimeMillis(), selectedCategory
                        )
                    }

                    user?.uid?.let {
                        post?.let { it1 ->
                            firestore.collection("Users").document(it).collection("Tickets")
                                .document().set(it1)
                                .addOnCompleteListener { posted ->
                                    binding.postButton.text = "Post"
                                    if (posted.isSuccessful) {
                                        Toast.makeText(
                                            activity,
                                            "Ticket created",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        fragmentManager?.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Error occurred. Please try again.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }

                    user?.uid?.let {
                        post?.let { it1 ->
                            firestore.collection("Tickets").document().set(it1)
                        }
                    }
                } else {
                    binding.postButton.text = "Post"
                    Log.d(CreatePostActivity.TAG, urlTaskCompleted.exception.toString())
                }
        }
    }
}
}