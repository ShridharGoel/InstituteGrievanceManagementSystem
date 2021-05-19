package com.shridhar.institutegrievancemanagementapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.socialmediaproject.models.Comment

class CommentsAdapter(options: FirestoreRecyclerOptions<Comment>, val context: Context):
    FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentsViewHolder>(options) {

    class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentAuthor: TextView = itemView.findViewById(R.id.commentAuthor)
        val commentTime: TextView = itemView.findViewById(R.id.commentTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentsViewHolder(
            holder
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comment) {
        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.commentText.text = model.text
        holder.commentAuthor.text = model.author.name
        holder.commentTime.text = dateFormatted.toString()
    }
}