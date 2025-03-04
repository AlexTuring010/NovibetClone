package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var userViewModel: UserViewModel
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var dateTextView: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)

        userViewModel = (requireActivity().application as MyApplication).userViewModel

        val totalBalanceTextView: TextView = view.findViewById(R.id.total_balance)
        val budget: TextView = view.findViewById(R.id.budget)
        val username: TextView = view.findViewById(R.id.profile_name)
        val riskLevel: TextView = view.findViewById(R.id.risk_level)

        userViewModel.budget.observe(viewLifecycleOwner, Observer { newBudget ->
            val integerPart = newBudget?.toInt() ?: 0
            budget.text = "$integerPart€"
        })

        userViewModel.flag_percentage.observe(viewLifecycleOwner, Observer { newPercentage ->
            val percentageString = if (newPercentage != null) {
                "${(newPercentage * 100).toInt()}%"
            } else {
                "N/A"
            }
            riskLevel.text = percentageString
        })

        userViewModel.balance.observe(viewLifecycleOwner, Observer { newBalance ->
            totalBalanceTextView.text = "${newBalance}€"
        })

        userViewModel.username.observe(viewLifecycleOwner, Observer { newUsername ->
            username.text = "$newUsername"
        })

        val logout: TextView = view.findViewById(R.id.logout)
        logout.setOnClickListener{
            dismiss()
            userViewModel.logout()
        }

        dateTextView = view.findViewById(R.id.date)
        dateTextView.setOnClickListener { showDatePicker() }

        userViewModel.date.observe(viewLifecycleOwner, Observer { newDate ->
            dateTextView.text = "$newDate"
        })

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        // Parse the date from userViewModel.data
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = userViewModel.date.value?.let {
            dateFormat.parse(it)
        } ?: Date() // Fallback to today's date if userViewModel.data is null

        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                userViewModel.updateData(dateFormat.format(calendar.time))
            },
            year, month, day
        ).show()
    }
}