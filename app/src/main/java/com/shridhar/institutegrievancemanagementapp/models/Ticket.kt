package com.shridhar.institutegrievancemanagementapp.models

data class Ticket(val text: String = "",
                  val imageUrl: String? = null,
                  val author: User = User(),
                  val time: Long = 0L,
                  val category: String = "Others",
                  val status: String = "Open")