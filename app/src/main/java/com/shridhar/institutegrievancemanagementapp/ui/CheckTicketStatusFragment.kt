package com.shridhar.institutegrievancemanagementapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.adapters.TicketsAdapter
import com.shridhar.institutegrievancemanagementapp.models.Ticket
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils

class CheckTicketStatusFragment : Fragment() {


    lateinit var adapter: TicketsAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_ticket_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.feedRecyclerView)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Users").document(UserUtils.user?.uid!!).collection("Tickets")
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Ticket>().setQuery(query, Ticket::class.java).build()

        adapter = TicketsAdapter(
            recyclerViewOptions,
            context!!
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
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