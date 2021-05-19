package com.shridhar.socialmediaproject.models

import com.shridhar.institutegrievancemanagementapp.models.User

data class Comment(val text: String = "",
                   val author: User = User(),
                   val time: Long = 0L)