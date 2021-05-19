package com.shridhar.institutegrievancemanagementapp.models

data class User(val uid: String = "",
                val name: String = "",
                val email: String = "",
                val following: MutableList<String> = mutableListOf(),
                val followers: MutableList<String> = mutableListOf(),
                val bio: String = "",
                val imageUrl: String = "",
                val isAdmin: Boolean = false)