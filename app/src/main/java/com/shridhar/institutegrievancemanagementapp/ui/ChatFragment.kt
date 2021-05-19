package com.shridhar.institutegrievancemanagementapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils
import com.shridhar.institutegrievancemanagementapp.adapters.ChatAdapter
import com.shridhar.institutegrievancemanagementapp.models.Chat

class ChatFragment : Fragment() {

    var chatroomId: String? = null

    lateinit var adapter: ChatAdapter
    lateinit var chatRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments

        if (bundle != null) {
            chatroomId = bundle.getString("chatroomId")
        }

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)

        setUpRecyclerView()

        val sendMessage: ImageView = view.findViewById(R.id.sendMessage)
        val enterMessage: EditText = view.findViewById(R.id.enterMessage)

        sendMessage.setOnClickListener {
            if(enterMessage.text.isNullOrEmpty()) {
                return@setOnClickListener
            }

            val chatText = enterMessage.text.toString()

            val firestore = FirebaseFirestore.getInstance().collection("Chatrooms")
                .document(chatroomId!!).collection("Messages")

            val chat = Chat(chatText, UserUtils.user!!, System.currentTimeMillis(), chatroomId!!)

            firestore.document().set(chat).addOnCompleteListener {
                chatRecyclerView.scrollToPosition(chatRecyclerView.adapter?.itemCount!! - 1)
                enterMessage.text.clear()
            }
        }
    }

    fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Chatrooms").document(chatroomId!!).collection("Messages")
            .orderBy("time", Query.Direction.ASCENDING)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()

        adapter = ChatAdapter(recyclerViewOptions)

        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}