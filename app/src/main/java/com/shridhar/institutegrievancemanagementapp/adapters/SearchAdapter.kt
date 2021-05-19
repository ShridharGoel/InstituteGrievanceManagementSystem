package com.shridhar.institutegrievancemanagementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.models.User
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(options: FirestoreRecyclerOptions<User>):
    FirestoreRecyclerAdapter<User, SearchAdapter.SearchViewHolder>(options) {

    class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userImage: CircleImageView = itemView.findViewById(R.id.profileImage)
        val nameText: TextView = itemView.findViewById(R.id.userName)
        val followButton: Button = itemView.findViewById(R.id.followButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return SearchViewHolder(
            holder
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int, model: User) {
        holder.nameText.text = model.name

        if (model.following.contains(snapshots.getSnapshot(holder.adapterPosition).id)) {
            holder.followButton.text = "Following"
        } else {
            holder.followButton.text = "Follow"
        }

        holder.followButton.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val userDocument = firestore.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid!!)

            userDocument.get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val user = it.result?.toObject(User::class.java)

                    if (holder.followButton.text == "Following") {
                        user?.following?.remove(snapshots.getSnapshot(holder.adapterPosition).id)
                        userDocument.set(user!!)
                        holder.followButton.text = "Follow"
                    } else {
                        user?.following?.add(snapshots.getSnapshot(holder.adapterPosition).id)
                        userDocument.set(user!!)
                        holder.followButton.text = "Following"
                    }
                } else {
                    println(it.exception)
                }
            }
        }
    }
}