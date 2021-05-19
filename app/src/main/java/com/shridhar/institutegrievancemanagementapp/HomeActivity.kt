package com.shridhar.institutegrievancemanagementapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.shridhar.institutegrievancemanagementapp.ui.*
import com.shridhar.institutegrievancemanagementapp.utils.UserUtils

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        UserUtils.getCurrentUser()

        setFragment(FeedFragment())

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feed_item -> {
                    setFragment(FeedFragment())
                }
                R.id.search_item -> {
                    setFragment(SearchFragment())
                }
                R.id.complaint_item -> {
                    setFragment(ComplaintFragment())
                }
                R.id.chatroom_item -> {
                    setFragment(ChatroomFragment())
                }
                R.id.profile_item -> {
                    setFragment(ProfileFragment())
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}