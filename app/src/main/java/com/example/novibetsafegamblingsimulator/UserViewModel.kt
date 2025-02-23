package com.example.novibetsafegamblingsimulator

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Callback
import okhttp3.Call
import java.io.IOException
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import android.util.Log
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val calendar: Calendar = Calendar.getInstance()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _balance = MutableLiveData<Float?>()
    val balance: MutableLiveData<Float?> get() = _balance

    private val _username = MutableLiveData<String?>()
    val username: MutableLiveData<String?> get() = _username

    private val _budget = MutableLiveData<Float?>()
    val budget: MutableLiveData<Float?> get() = _budget

    private val _date = MutableLiveData<String?>()
    val date: MutableLiveData<String?> get() = _date

    private val sharedPreferencesHelper = SharedPreferencesHelper(application)
    private val userRepository = UserRepository()

    init {
        val savedUser = sharedPreferencesHelper.getUser()
        val savedDate = sharedPreferencesHelper.getDate()
        if(savedDate != null) {
            _date.value = savedDate
        } else{
            _date.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time).toString()
        }
        if (savedUser != null) {
            _user.value = savedUser
            _balance.value = savedUser.total_remain
            _username.value = savedUser.username
            _isLoggedIn.value = true
            updateBudget()
        } else {
            _isLoggedIn.value = false
        }

    }

    fun updateBudget() {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
            .build()

        // Parse the input date
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(_date.value, dateFormatter)
        Log.d("updateBudget", "Parsed date: $parsedDate")

        // Determine the first day of the week (Monday)
        val firstDayOfWeek = parsedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        Log.d("updateBudget", "First day of the week: $firstDayOfWeek")

        // Format the first day of the week to the required string format
        val formattedFirstDayOfWeek = firstDayOfWeek.format(dateFormatter)
        Log.d("updateBudget", "Formatted first day of the week: $formattedFirstDayOfWeek")

        // Construct the URL with the customer_id and the first day of the week date
        val url = "https://ctrl-alt-dit-yb2k.onrender.com/predict_budget?customer_id=${_user.value?.customer_id}&date=${formattedFirstDayOfWeek}"
        Log.d("updateBudget", "Constructed URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("updateBudget", "Request failed", e)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("updateBudget", "Unexpected response code: ${response.code}")
                    return
                }

                val responseData = response.body?.string()
                Log.d("updateBudget", "Response data: $responseData")

                if (responseData != null) {
                    try {
                        val jsonObject = JSONObject(responseData)
                        val predictedBudget = jsonObject.getString("predicted_budget").toFloatOrNull()
                        if (predictedBudget != null) {
                            budget.postValue(predictedBudget)
                            Log.d("updateBudget", "Updated budget: ${budget.value}")
                        } else {
                            Log.e("updateBudget", "Invalid predicted_budget value")
                        }
                    } catch (e: Exception) {
                        Log.e("updateBudget", "Error parsing JSON response", e)
                    }
                } else {
                    Log.e("updateBudget", "Response data is null")
                }
            }
        })
    }

    fun login(user: User) {
        _user.value = user
        _balance.value = user.total_remain
        _isLoggedIn.value = true
        _username.value = user.username
        sharedPreferencesHelper.saveUser(user)
        updateBudget()
    }

    fun logout() {
        _user.value = null
        _balance.value = null
        _username.value = null
        _isLoggedIn.value = false
        sharedPreferencesHelper.clearUser()
    }

    fun updateBalance(newBalance: Float) {
        balance.value = newBalance
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

    fun updateData(newDate: String) {
        date.value = newDate
        sharedPreferencesHelper.saveDate(newDate)
        updateBudget()
    }
}