package com.shridhar.institutegrievancemanagementapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shridhar.institutegrievancemanagementapp.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterPresenter(view: View) {

    private var view: View? = null

    init {
        this.view = view
    }

    fun signup(email: String, name: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(
                            auth.currentUser?.uid!!,
                            name,
                            email
                        )
                        val firestore = FirebaseFirestore.getInstance().collection("Users")
                        firestore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    view?.goToHomeActivity()
                                } else {
                                    view?.showError()
                                }
                            }
                    } else {
                        view?.showError()
                    }
                }
        }
    }

    interface View {
        fun goToHomeActivity()
        fun showError()
    }
}