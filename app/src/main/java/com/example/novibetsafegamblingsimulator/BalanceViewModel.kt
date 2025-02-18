package com.example.novibetsafegamblingsimulator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val _balance = MutableLiveData<String>()
    val balance: LiveData<String> get() = _balance

    fun setBalance(newBalance: String) {
        _balance.value = newBalance
    }
}