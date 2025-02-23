package com.example.novibetsafegamblingsimulator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlotGameDetailActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var tossCoinButton: Button
    private lateinit var betButton: Button
    private lateinit var toggleHeadTail: ToggleButton
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var buttonClickMediaPlayer: MediaPlayer
    private lateinit var loginButton: AppCompatButton
    private lateinit var music_on: ImageView
    private lateinit var music_off: ImageView
    private lateinit var userViewModel: UserViewModel
    private lateinit var betResultAdapter: BetResultAdapter
    private val userRepository = UserRepository()

    private val betValues = listOf("5€", "10€", "20€", "50€", "100€", "150€", "200€")
    private var currentBetIndex = 0

    private lateinit var backButton: FrameLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val gameName = intent.getStringExtra("gameName")
        val studioName = intent.getStringExtra("studioName")

        backButton = findViewById(R.id.game_activity_back_button)

        backButton.setOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }

        videoView = findViewById(R.id.video_view)
        tossCoinButton = findViewById(R.id.toss_coin_button)
        betButton = findViewById(R.id.bet_button)
        toggleHeadTail = findViewById(R.id.toggle_head_tail)

        val profileIcon: FrameLayout = findViewById(R.id.profile_pic)
        profileIcon.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val downArrow: FrameLayout = findViewById(R.id.arrow_down)
        downArrow.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val remainAmountFrame: FrameLayout = findViewById(R.id.remain_amount2_frame)

        remainAmountFrame.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val remainAmountTextView: TextView = findViewById(R.id.remain_amount2)

        // Get the shared UserViewModel from the Application
        userViewModel = (application as MyApplication).userViewModel

        userViewModel.balance.observe(this, Observer { newBalance ->
            remainAmountTextView.text = "$newBalance€"
        })


        val depositButton: Button = findViewById(R.id.deposit_button)
        depositButton.setOnClickListener {
            showPopup()
        }

        loginButton = findViewById(R.id.login_button)
        val registerButton: AppCompatButton = findViewById(R.id.register_button)

        // Observe the login state
        userViewModel.isLoggedIn.observe(this, Observer { isLoggedIn ->
            if (isLoggedIn) {
                depositButton.visibility = View.VISIBLE
                profileIcon.visibility = View.VISIBLE
                remainAmountFrame.visibility = View.VISIBLE
                downArrow.visibility = View.VISIBLE
                loginButton.visibility = View.GONE
                registerButton.visibility = View.GONE
            } else {
                depositButton.visibility = View.GONE
                profileIcon.visibility = View.GONE
                remainAmountFrame.visibility = View.GONE
                downArrow.visibility = View.GONE
                loginButton.visibility = View.VISIBLE
                registerButton.visibility = View.VISIBLE
            }
        })

        loginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                .add(android.R.id.content, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        }

        // Set the video URI (replace with your video file path or URL)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.coin_flip5)
        videoView.setVideoURI(videoUri)

        // Seek to the first frame to make the video visible
        videoView.setOnPreparedListener {
            videoView.seekTo(1)
            adjustVideoViewScaling()
        }

        // Initialize the MediaPlayer for button click sound effect
        buttonClickMediaPlayer = MediaPlayer.create(this, R.raw.button_click)

        tossCoinButton.setOnClickListener {
            animateButtonClick(it)
            // Check if the user is logged in
            if (userViewModel.isLoggedIn.value != true) {
                Toast.makeText(this, "Πρέπει να συνδεθείς για να παίξεις.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Get the current balance
            val currentBalance = userViewModel.balance.value ?: 0.0f
            val betAmount = betValues[currentBetIndex].replace("€", "").toFloat()
            // Check if the balance is sufficient
            if (currentBalance < betAmount) {
                Toast.makeText(this, "Το υπόλοιπο σου δεν επαρκεί για αυτό το στοίχημα.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            playButtonClickSound()
            videoView.start()
        }

        betButton.setOnClickListener {
            animateButtonClick(it)
            playButtonClickSound()
            // Cycle through bet values
            currentBetIndex = (currentBetIndex + 1) % betValues.size
            betButton.text = betValues[currentBetIndex]
        }

        toggleHeadTail.setOnClickListener {
            animateButtonClick(it)
            playButtonClickSound()
            // Add your toggle button logic here
        }

        // Initialize RecyclerView
        val betResultRecyclerView: RecyclerView = findViewById(R.id.bet_result_recycler_view)
        betResultRecyclerView.layoutManager = LinearLayoutManager(this)
        betResultAdapter = BetResultAdapter(mutableListOf())
        betResultRecyclerView.adapter = betResultAdapter

        videoView.setOnCompletionListener {
            videoView.seekTo(0)
            videoView.pause()
            // Display the result of the coin toss here
            val result = if ((0..1).random() < 0.5) "Γράμματα" else "Κορώνα"
            val isWin = (result == "Κορώνα" && toggleHeadTail.isChecked) || (result == "Γράμματα" && !toggleHeadTail.isChecked)
            val betAmount = betValues[currentBetIndex].replace("€", "").toFloat()
            val betResult = BetResult(result, isWin, betAmount)
            betResultAdapter.addBetResult(betResult)
            betResultRecyclerView.scrollToPosition(betResultAdapter.itemCount - 1)
            val currentBalance: Float?
            val newBalance: Float?
            if(isWin){
                currentBalance = userViewModel.balance.value ?: 0.0f
                newBalance = currentBalance + betAmount
                userViewModel.updateBalance(newBalance, true)
            } else{
                currentBalance = userViewModel.balance.value ?: 0.0f
                newBalance = currentBalance - betAmount
                userViewModel.updateBalance(newBalance, true)
            }
            val user: User? = userViewModel.user.value
            val date: String? = userViewModel.date.value
            userViewModel.viewModelScope.launch {
                if (user != null) {
                    delay(2000) // 2-second cooldown before making the request
                    userRepository.insertTransaction(user.customer_id, date, 0, currentBalance, newBalance, 1, betAmount, isWin)
                }
            }
        }

        // Initialize and start the MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Register fragment lifecycle callbacks
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)

        music_on = findViewById(R.id.volume_on)
        music_off = findViewById(R.id.volume_off)
        music_on.setOnClickListener {
            mediaPlayer.setVolume(0.0f, 0.0f)
            music_on.visibility = View.GONE
            music_off.visibility = View.VISIBLE
        }
        music_off.setOnClickListener {
            mediaPlayer.setVolume(0.5f, 0.5f)
            music_on.visibility = View.VISIBLE
            music_off.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause the MediaPlayer for background music
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume the MediaPlayer for background music
        if (this::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop and release the MediaPlayer for background music
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        // Release the MediaPlayer for button click sound effect
        if (this::buttonClickMediaPlayer.isInitialized) {
            buttonClickMediaPlayer.release()
        }
        // Unregister fragment lifecycle callbacks
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            if (f is LoginFragment) {
                mediaPlayer.pause()
                backButton.isClickable = false
                loginButton.isClickable = false
                tossCoinButton.isClickable = false
                betButton.isClickable = false
                toggleHeadTail.isClickable = false
                music_on.isClickable = false
                music_off.isClickable = false
            }
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            if (f is LoginFragment) {
                mediaPlayer.start()
                backButton.isClickable = true
                loginButton.isClickable = true
                tossCoinButton.isClickable = true
                betButton.isClickable = true
                toggleHeadTail.isClickable = true
                music_on.isClickable = true
                music_off.isClickable = true
            }
        }
    }

    private fun animateButtonClick(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f)
        scaleX.duration = 100
        scaleY.duration = 100

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }

    private fun playButtonClickSound() {
        if (this::buttonClickMediaPlayer.isInitialized) {
            buttonClickMediaPlayer.start()
        }
    }

    private fun adjustVideoViewScaling() {
        val videoViewParent = videoView.parent as View
        val videoViewParams = videoView.layoutParams as FrameLayout.LayoutParams
        val videoAspectRatio = videoView.width.toFloat() / videoView.height.toFloat()
        val parentAspectRatio = videoViewParent.width.toFloat() / videoViewParent.height.toFloat()

        if (videoAspectRatio > parentAspectRatio) {
            videoViewParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            videoViewParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        } else {
            videoViewParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
            videoViewParams.height = FrameLayout.LayoutParams.MATCH_PARENT
        }

        videoView.layoutParams = videoViewParams
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_money_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val closeButton: ImageView = dialog.findViewById(R.id.close_icon)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val confirmButton: Button = dialog.findViewById(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val amountSpinner: Spinner = dialog.findViewById(R.id.amount_spinner)
            val selectedAmountString = amountSpinner.selectedItem.toString()
            val selectedAmount = selectedAmountString
                .replace(",", ".")
                .replace("$", "")
                .toFloatOrNull() ?: 0.0f

            // Get the current balance
            val currentBalance = userViewModel.balance.value ?: 0.0f

            // Update the balance
            val newBalance = currentBalance + selectedAmount

            val user: User? = userViewModel.user.value
            val date: String? = userViewModel.date.value
            userViewModel.viewModelScope.launch {
                if (user != null) {
                    userRepository.insertTransaction(user.customer_id, date, 1, currentBalance, newBalance, null, null, null)
                }
            }

            userViewModel.updateBalance(newBalance, false)
            dialog.dismiss()
        }

        val amountSpinner: Spinner = dialog.findViewById(R.id.amount_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.amount_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            amountSpinner.adapter = adapter
        }

        dialog.show()
    }
}