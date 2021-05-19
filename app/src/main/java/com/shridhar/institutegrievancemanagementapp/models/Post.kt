package com.shridhar.institutegrievancemanagementapp.models

data class Post(val text: String = "",
                val imageUrl: String? = null,
                val author: User = User(),
                val time: Long = 0L,
                val likesList: MutableList<String> = mutableListOf(),
                val visibility: String = "Public")