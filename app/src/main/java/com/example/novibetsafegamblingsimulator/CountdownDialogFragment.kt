package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager
import android.widget.FrameLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CountdownDialogFragment : DialogFragment() {

    private lateinit var countdownTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var endDate: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_countdown_dialog, container, false)
        countdownTextView = view.findViewById(R.id.countdownTextView)
        messageTextView = view.findViewById(R.id.messageTextView)

        // Get the end date from arguments
        val endDateString = arguments?.getString("end_date")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        endDate = dateFormat.parse(endDateString) ?: Date()

        startCountdown()

        val closeButton: FrameLayout = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun startCountdown() {
        val currentTime = System.currentTimeMillis()
        val endTime = endDate.time
        val timeRemaining = endTime - currentTime

        object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                countdownTextView.text = String.format(
                    "%02d μέρες %02d ώρες %02d λεπτά %02d δευτερόλεπτα",
                    days, hours, minutes, seconds
                )
            }

            override fun onFinish() {
                countdownTextView.text = "Time's up!"
                // Optionally, you can dismiss the dialog here
                // dismiss()
            }
        }.start()
    }

    companion object {
        fun newInstance(endDate: String): CountdownDialogFragment {
            val fragment = CountdownDialogFragment()
            val args = Bundle()
            args.putString("end_date", endDate)
            fragment.arguments = args
            return fragment
        }
    }
}