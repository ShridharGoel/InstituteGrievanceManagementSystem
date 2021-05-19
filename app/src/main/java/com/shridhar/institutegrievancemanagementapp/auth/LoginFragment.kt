package com.shridhar.institutegrievancemanagementapp.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.shridhar.institutegrievancemanagementapp.HomeActivity
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.databinding.LoginFragmentBinding

class LoginFragment : Fragment(), LoginPresenter.View {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var presenter: LoginPresenter

    companion object {
        const val TAG = "LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = LoginPresenter(this, context!!)

        if (arguments?.getString("Source") == "Student") {
            showStudentLogin()
        } else {
            showAdminLogin()
        }

        binding.goToRegister.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(
                    R.id.auth_fragment_container,
                    RegisterFragment()
                )
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailText.editText?.text.toString()
            val password = binding.passwordText.editText?.text.toString()

            binding.emailText.error = null
            binding.passwordText.error = null

            if (TextUtils.isEmpty(email)) {
                binding.emailText.error = "Email is required"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailText.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                binding.passwordText.error = "Password is required"
                return@setOnClickListener
            }

            binding.loginProgress.visibility = View.VISIBLE

            presenter.login(email, password)
        }
    }

    private fun showStudentLogin() {
        binding.loginText.text = "Student Login"
    }

    private fun showAdminLogin() {
        binding.loginText.text = "Admin Login"
    }

    override fun goToHomeActivity() {
        binding.loginProgress.visibility = View.GONE
        startActivity(Intent(activity, HomeActivity::class.java))
    }

    override fun showError() {
        binding.loginProgress.visibility = View.GONE
        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
    }
}