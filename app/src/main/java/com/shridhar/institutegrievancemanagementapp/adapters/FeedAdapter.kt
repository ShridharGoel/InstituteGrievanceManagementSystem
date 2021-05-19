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

class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context):
    FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewHolder>(options) {

    class FeedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postText: TextView = itemView.findViewById(R.id.postText)
        val authorText: TextView = itemView.findViewById(R.id.postAuthor)
        val timeText: TextView = itemView.findViewById(R.id.postTime)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val visibilityText: TextView = itemView.findViewById(R.id.visibilityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return FeedViewHolder(
            holder
        )
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, model: Post) {

        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.postText.text = model.text
        holder.authorText.text = model.author.name
        holder.timeText.text = dateFormatted.toString()
        holder.likeCount.text = model.likesList.size.toString()
        holder.visibilityText.text = model.visibility

        Glide.with(context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder_image)
            .into(holder.postImage)

        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val postDocument = firestore.collection("Posts").document(snapshots.getSnapshot(holder.adapterPosition).id)

        postDocument.collection("Comments").get().addOnCompleteListener {
            if(it.isSuccessful) {
                holder.commentCount.text = it.result?.size().toString()
            }
        }

        postDocument.get().addOnCompleteListener {
            if(it.isSuccessful) {
                val post = it.result?.toObject(Post::class.java)

                if (post?.likesList?.contains(userId)!!) {
                    holder.likeIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.like_icon_filled
                        )
                    )
                } else {
                    holder.likeIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.like_icon
                        )
                    )
                }

                holder.likeIcon.setOnClickListener {
                    if (post.likesList.contains(userId)) {
                        post.likesList.remove(userId)
                        holder.likeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                            R.drawable.like_icon
                        ))
                    } else {
                        post.likesList.add(userId!!)
                        holder.likeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                            R.drawable.like_icon_filled
                        ))
                    }

                    postDocument.set(post)
                }
            } else {
                println(it.exception)
            }
        }

        holder.commentIcon.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("postId", snapshots.getSnapshot(holder.adapterPosition).id)
            context.startActivity(intent)
        }
    }
}