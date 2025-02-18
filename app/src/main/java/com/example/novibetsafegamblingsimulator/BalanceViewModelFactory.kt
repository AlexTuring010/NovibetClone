package com.example.novibetsafegamblingsimulator

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BalanceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var INSTANCE: BalanceViewModelFactory? = null

        fun getInstance(application: Application): BalanceViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BalanceViewModelFactory(application).also { INSTANCE = it }
            }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BalanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BalanceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}