package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var userViewModel: UserViewModel

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

        return view
    }
}