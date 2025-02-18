package com.example.novibetsafegamblingsimulator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SlotGameDetailActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var tossCoinButton: Button
    private lateinit var betButton: Button
    private lateinit var toggleHeadTail: ToggleButton
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var buttonClickMediaPlayer: MediaPlayer

    private lateinit var balanceViewModel: BalanceViewModel

    private val betValues = listOf("5€", "10€", "20€", "50€", "100€", "150€", "200€")
    private var currentBetIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val gameName = intent.getStringExtra("gameName")
        val studioName = intent.getStringExtra("studioName")

        val backButton: FrameLayout = findViewById(R.id.game_activity_back_button)

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

        val down_arrow: FrameLayout = findViewById(R.id.arrow_down)
        down_arrow.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val remainAmountFrame: FrameLayout = findViewById(R.id.remain_amount2_frame)

        remainAmountFrame.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val remainAmountTextView: TextView = findViewById(R.id.remain_amount2)

        // Get the shared BalanceViewModel from the Application
        balanceViewModel = (application as MyApplication).balanceViewModel

        // Observe the balance amount
        balanceViewModel.balance.observe(this, Observer { newBalance ->
            remainAmountTextView.text = newBalance
        })

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

        videoView.setOnCompletionListener {
            videoView.seekTo(0)
            videoView.pause()
            // Display the result of the coin toss here
            val result = if (Math.random() < 0.5) "Heads" else "Tails"
        }

        // Initialize and start the MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.the_gambler)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
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
}