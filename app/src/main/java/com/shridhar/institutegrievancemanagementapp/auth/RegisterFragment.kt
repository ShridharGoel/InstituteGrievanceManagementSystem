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
import com.shridhar.institutegrievancemanagementapp.databinding.RegisterFragmentBinding

class RegisterFragment : Fragment(), RegisterPresenter.View {

    private lateinit var binding: RegisterFragmentBinding
    private lateinit var presenter: RegisterPresenter

    companion object {
        const val TAG = "RegisterFragment"
    }

    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = RegisterPresenter(this)

        binding.signupButton.setOnClickListener {
            val email = binding.emailText.editText?.text.toString()
            val name = binding.nameText.editText?.text.toString()
            val password = binding.passwordText.editText?.text.toString()
            val confirmPassword = binding.confirmPasswordText.editText?.text.toString()

            binding.apply {
                emailText.error = null
                nameText.error = null
                passwordText.error = null
                confirmPasswordText.error = null
            }

            if (TextUtils.isEmpty(email)) {
                binding.emailText.error = "Email is required"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailText.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(name)) {
                binding.nameText.error = "Name is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                binding.passwordText.error = "Password is required"
                return@setOnClickListener
            }

            if (!password.matches(passwordRegex)) {
                binding.passwordText.error = "Password should contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                binding.confirmPasswordText.error = "Confirm password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.confirmPasswordText.error = "Passwords do not match"
                return@setOnClickListener
            }

            binding.signUpProgress.visibility = View.VISIBLE

            presenter.signup(email, name, password)
        }

        binding.goToLogin.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(
                    R.id.auth_fragment_container,
                    LoginFragment()
                )
                ?.commit()
        }
    }

    override fun goToHomeActivity() {
        binding.signUpProgress.visibility = View.GONE
        startActivity(Intent(activity, HomeActivity::class.java))
    }

    override fun showError() {
        binding.signUpProgress.visibility = View.GONE
        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
    }
}