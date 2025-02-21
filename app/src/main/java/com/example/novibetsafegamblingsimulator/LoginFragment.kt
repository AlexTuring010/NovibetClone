package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var errorMessage: TextView
    private lateinit var errorFrame: LinearLayout
    private lateinit var userViewModel: UserViewModel
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        userViewModel = (requireActivity().application as MyApplication).userViewModel

        usernameLayout = view.findViewById(R.id.username_layout)
        passwordLayout = view.findViewById(R.id.password_layout)
        usernameEditText = view.findViewById(R.id.username_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        errorMessage = view.findViewById(R.id.error_message)
        errorFrame = view.findViewById(R.id.error_frame)

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the error state when the user starts typing
                if (passwordLayout.isErrorEnabled) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })

        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the error state when the user starts typing
                if (usernameLayout.isErrorEnabled) {
                    usernameLayout.error = null
                    usernameLayout.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })

        val backArrow: ImageView = view.findViewById(R.id.back_arrow2)
        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(0, R.anim.slide_out_up)
                .remove(this)
                .commit()
        }

        val loginButton: AppCompatButton = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            validateAndLogin()
        }

        return view
    }

    private fun validateAndLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        var isValid = true

        if (username.isEmpty()) {
            errorFrame.visibility = View.VISIBLE
            errorMessage.text = "Το πεδίο δεν μπορεί να είναι κενό"
            usernameLayout.setError(" ")
            isValid = false
        } else {
            usernameLayout.error = null
        }

        if (password.isEmpty()) {
            errorFrame.visibility = View.VISIBLE
            errorMessage.text = "Το πεδίο δεν μπορεί να είναι κενό"
            passwordLayout.setError(" ")
            isValid = false
        } else {
            passwordLayout.error = null
        }

        if (isValid) {
            lifecycleScope.launch {
                val user = userRepository.authenticateUser(username, password)
                if (user != null) {
                    errorFrame.visibility = View.GONE
                    usernameLayout.error = null
                    passwordLayout.error = null
                    userViewModel.login(user)
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(0, R.anim.slide_out_up)
                        .remove(this@LoginFragment)
                        .commit()
                } else {
                    errorFrame.visibility = View.VISIBLE
                    errorMessage.text = "Λυπούμαστε αλλά δεν μπορέσαμε να βρούμε λογαριασμό που να σχετίζεται με αυτά τα στοιχεία."
                    usernameLayout.setError(" ")
                    passwordLayout.setError(" ")
                }
            }
        }
    }
}