package com.example.novibetsafegamblingsimulator

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

class MyApplication : Application() {

    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    val balanceViewModel: BalanceViewModel by lazy {
        ViewModelProvider(
            appViewModelStore,
            BalanceViewModelFactory.getInstance(this)
        ).get(BalanceViewModel::class.java)
    }
}