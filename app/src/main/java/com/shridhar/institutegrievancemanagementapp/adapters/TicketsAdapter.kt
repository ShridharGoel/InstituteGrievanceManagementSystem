package com.shridhar.institutegrievancemanagementapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.ui.CommentsActivity
import com.shridhar.institutegrievancemanagementapp.models.Post
import com.shridhar.institutegrievancemanagementapp.models.Ticket
import org.w3c.dom.Text

class TicketsAdapter(options: FirestoreRecyclerOptions<Ticket>, val context: Context):
    FirestoreRecyclerAdapter<Ticket, TicketsAdapter.TicketViewHolder>(options) {

    class TicketViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postText: TextView = itemView.findViewById(R.id.postText)
        val authorText: TextView = itemView.findViewById(R.id.postAuthor)
        val timeText: TextView = itemView.findViewById(R.id.postTime)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val visibilityIcon: ImageView = itemView.findViewById(R.id.visibilityIcon)
        val visibilityText: TextView = itemView.findViewById(R.id.visibilityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return TicketViewHolder(
            holder
        )
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int, model: Ticket) {

        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.postText.text = model.text
        holder.authorText.text = model.status
        holder.timeText.text = dateFormatted.toString()
        holder.likeCount.visibility = View.GONE

        Glide.with(context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder_image)
            .into(holder.postImage)

        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val postDocument = firestore.collection("Tickets").document(snapshots.getSnapshot(holder.adapterPosition).id)

        postDocument.collection("Comments").get().addOnCompleteListener {
            if(it.isSuccessful) {
                holder.commentCount.text = it.result?.size().toString()
            }
        }

        holder.likeIcon.visibility = View.GONE

        holder.visibilityIcon.visibility = View.GONE
        holder.visibilityText.visibility = View.GONE

        holder.commentIcon.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("postId", snapshots.getSnapshot(holder.adapterPosition).id)
            context.startActivity(intent)
        }
    }
}