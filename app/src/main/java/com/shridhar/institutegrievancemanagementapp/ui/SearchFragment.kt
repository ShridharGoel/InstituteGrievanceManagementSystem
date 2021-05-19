package com.shridhar.institutegrievancemanagementapp.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.HomeActivity
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils
import com.shridhar.institutegrievancemanagementapp.adapters.SearchAdapter
import com.shridhar.institutegrievancemanagementapp.models.User

class SearchFragment : Fragment() {

    lateinit var adapter: SearchAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.searchToolbar)
        toolbar.title = "Search Users"
        (activity as? HomeActivity)?.setSupportActionBar(toolbar)
        (activity as? HomeActivity)?.supportActionBar?.show()

        setHasOptionsMenu(true)

        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Users").whereNotEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        adapter = SearchAdapter(
            recyclerViewOptions
        )

        recyclerView = view.findViewById(R.id.searchRecyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchView = SearchView(context!!)
        menu.findItem(R.id.action_search).actionView = searchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerView.visibility = View.VISIBLE
                val firestore = FirebaseFirestore.getInstance()
                val newQuery = firestore.collection("Users").whereEqualTo("name", query)
                    .whereNotEqualTo("uid", UserUtils.user?.uid)

                val newRecyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(newQuery, User::class.java).build()

                adapter.updateOptions(newRecyclerViewOptions)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerView.visibility = View.INVISIBLE
                return false
            }

        })
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