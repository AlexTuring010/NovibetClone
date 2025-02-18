package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var balanceViewModel: BalanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)
    
        balanceViewModel = (requireActivity().application as MyApplication).balanceViewModel
    
        val totalBalanceTextView: TextView = view.findViewById(R.id.total_balance)
    
        // Observe the balance amount
        balanceViewModel.balance.observe(viewLifecycleOwner, Observer { newBalance ->
            totalBalanceTextView.text = newBalance
        })
    
        return view
    }
}