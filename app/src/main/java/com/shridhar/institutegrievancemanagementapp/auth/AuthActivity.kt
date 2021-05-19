package com.shridhar.institutegrievancemanagementapp.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.shridhar.institutegrievancemanagementapp.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bundle = bundleOf("Source" to intent.getStringExtra("Source"))

        val loginFragment = LoginFragment()
        loginFragment.arguments = bundle

        if (intent.hasExtra("Registration")) {
            supportFragmentManager.beginTransaction()
                .add(R.id.auth_fragment_container, RegisterFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.auth_fragment_container, loginFragment)
                .commit()
        }
    }
}