package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var errorMessage: TextView
    private lateinit var errorFrame: LinearLayout
    private lateinit var userViewModel: UserViewModel
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userViewModel = (application as MyApplication).userViewModel

        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        usernameEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        errorMessage = findViewById(R.id.error_message)
        errorFrame = findViewById(R.id.error_frame)

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

        val closeIcon: ImageView = findViewById(R.id.close_icon)
        closeIcon.setOnClickListener {
            finish()
        }

        val registerButton: AppCompatButton= findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
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
                val user = userRepository.findUser(username)
                if (user == null) {
                    errorFrame.visibility = View.GONE
                    usernameLayout.error = null
                    passwordLayout.error = null
                    val newUser: User? = userRepository.createUser(username, password)
                    if(newUser != null){
                        userViewModel.login(newUser)
                        val user: User? = userViewModel.user.value
                        val date: String? = userViewModel.date.value
                        userViewModel.viewModelScope.launch {
                            if (user != null) {
                                userRepository.insertTransaction(user.customer_id, date, 3, 0.0f, 0.0f, null, null, null)
                            }
                        }
                        finish()
                    } else{
                        errorFrame.visibility = View.VISIBLE
                        errorMessage.text = "Υπήρξε κάποιο πρόβλημε με την βάση δεδομένων, δεν μπορέσαμε να δημιουργήσουμε τον λοαριασμό"
                        usernameLayout.setError(" ")
                        passwordLayout.setError(" ")
                    }
                } else {
                    errorFrame.visibility = View.VISIBLE
                    errorMessage.text = "Υπάρχει ήδη χρήστης με αυτό το όνομα."
                    usernameLayout.setError(" ")
                    passwordLayout.setError(" ")
                }
            }
        }
    }
}