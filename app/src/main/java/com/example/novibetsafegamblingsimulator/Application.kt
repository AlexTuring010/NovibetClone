package com.example.novibetsafegamblingsimulator

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

class MyApplication : Application() {

    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        ).get(UserViewModel::class.java)
    }
}