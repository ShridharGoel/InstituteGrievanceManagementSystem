package com.shridhar.institutegrievancemanagementapp.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginPresenter(view: View, context: Context) {

    private var view: View? = null

    init {
        this.view = view
        FirebaseApp.initializeApp(context)
    }

    fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        view?.goToHomeActivity()
                    } else {
                        view?.showError()
                        Log.d(LoginFragment.TAG, task.exception.toString())
                    }
                }
        }
    }

    interface View {
        fun goToHomeActivity()
        fun showError()
    }
}