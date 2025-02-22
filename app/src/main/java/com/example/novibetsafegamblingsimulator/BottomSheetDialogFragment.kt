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
import java.text.SimpleDateFormat
import java.util.*

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var userViewModel: UserViewModel
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var dateTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)

        userViewModel = (requireActivity().application as MyApplication).userViewModel

        val totalBalanceTextView: TextView = view.findViewById(R.id.total_balance)
        val username: TextView = view.findViewById(R.id.profile_name)

        userViewModel.balance.observe(viewLifecycleOwner, Observer { newBalance ->
            totalBalanceTextView.text = "$newBalance€"
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

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                userViewModel.updateData(dateFormat.format(calendar.time))
            },
            year, month, day
        ).show()
    }
}