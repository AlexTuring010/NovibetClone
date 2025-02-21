package com.example.novibetsafegamblingsimulator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _balance = MutableLiveData<Float?>()
    val balance: MutableLiveData<Float?> get() = _balance

    private val _username = MutableLiveData<String?>()
    val username: MutableLiveData<String?> get() = _username

    private val sharedPreferencesHelper = SharedPreferencesHelper(application)
    private val userRepository = UserRepository()

    init {
        val savedUser = sharedPreferencesHelper.getUser()
        if (savedUser != null) {
            _user.value = savedUser
            _balance.value = savedUser.total_remain
            _username.value = savedUser.username
            _isLoggedIn.value = true
        } else {
            _isLoggedIn.value = false
        }
    }

    fun login(user: User) {
        _user.value = user
        _balance.value = user.total_remain
        _isLoggedIn.value = true
        _username.value = user.username
        sharedPreferencesHelper.saveUser(user)
    }

    fun logout() {
        _user.value = null
        _balance.value = null
        _username.value = null
        _isLoggedIn.value = false
        sharedPreferencesHelper.clearUser()
    }

    fun updateBalance(newBalance: Float) {
        _balance.value = newBalance
        _user.value?.let {
            val updatedUser = it.copy(total_remain = newBalance)
            _user.value = updatedUser
            sharedPreferencesHelper.saveUser(updatedUser)

            // Update the balance in the database
            viewModelScope.launch {
                userRepository.updateBalance(it.customer_id, newBalance)
            }
        }
    }
}