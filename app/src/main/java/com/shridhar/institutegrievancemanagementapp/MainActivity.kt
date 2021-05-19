package com.shridhar.institutegrievancemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.shridhar.institutegrievancemanagementapp.auth.AuthActivity
import com.shridhar.institutegrievancemanagementapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.loginAsStudent.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("Source", "Student")
            startActivity(intent)
        }

        binding.loginAsAdmin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("Source", "Admin")
            startActivity(intent)
        }

        binding.registerAsStudent.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("Source", "Student")
            intent.putExtra("Registration", true)
            startActivity(intent)
        }
    }
}