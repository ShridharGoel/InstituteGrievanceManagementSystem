package com.shridhar.institutegrievancemanagementapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils
import com.shridhar.institutegrievancemanagementapp.adapters.CommentsAdapter
import com.shridhar.socialmediaproject.models.Comment

class CommentsActivity : AppCompatActivity() {
    private var postId: String? = null

    private var adapter: CommentsAdapter? = null
    private lateinit var commentsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        postId = intent.getStringExtra("postId")

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)

        setUpRecyclerView()

        val sendButton: ImageView = findViewById(R.id.sendComment)
        val commentEditText: EditText = findViewById(R.id.enterComment)

        sendButton.setOnClickListener {
            val commentText = commentEditText.text.toString()

            val firestore = FirebaseFirestore.getInstance()

            val comment = Comment(commentText, UserUtils.user!!, System.currentTimeMillis())
            firestore.collection("Posts").document(postId!!)
                .collection("Comments").document().set(comment)

            commentEditText.text.clear()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = postId?.let {
            firestore.collection("Posts").document(it).collection("Comments")
        }

        val recyclerViewOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it, Comment::class.java).build()
        }

        adapter = recyclerViewOptions?.let {
            CommentsAdapter(it, this)
        }

        commentsRecyclerView.adapter = adapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}