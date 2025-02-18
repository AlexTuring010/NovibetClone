package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var viewPagerAds: ViewPager2
    private lateinit var customTabLines: CustomTabLinesView
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var progress = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        window.navigationBarColor = ContextCompat.getColor(this, R.color.footer_background)

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

        val remainAmountFrame: FrameLayout = findViewById(R.id.remain_amount_frame)

        remainAmountFrame.setOnClickListener {
            val profileBottomSheet = ProfileBottomSheetFragment()
            profileBottomSheet.show(supportFragmentManager, profileBottomSheet.tag)
        }

        val remainAmountTextView: TextView = findViewById(R.id.remain_amount)

        // Get the shared BalanceViewModel from the Application
        balanceViewModel = (application as MyApplication).balanceViewModel

        // Observe the balance amount
        balanceViewModel.balance.observe(this, Observer { newBalance ->
            remainAmountTextView.text = newBalance
        })

        // Set initial balance
        balanceViewModel.setBalance("100€")

        val firstRecyclerView = findViewById<RecyclerView>(R.id.first_recycler_view)
        val secondRecyclerView = findViewById<RecyclerView>(R.id.second_recycler_view)
        viewPagerAds = findViewById(R.id.view_pager_ads)
        customTabLines = findViewById(R.id.custom_tab_lines)

        val firstItems = listOf(
            IconTextItem(R.drawable.n_xclusive_casino, "Novibet Exclusives"),
            IconTextItem(R.drawable.new_slots, "Νέα"),
            IconTextItem(R.drawable.romance, "Valentine's Slots"),
            IconTextItem(R.drawable.all_stars, "Top 10"),
            IconTextItem(R.drawable.popular, "Δημοφιλή"),
            IconTextItem(R.drawable.playtechsvg, "Premium"),
            IconTextItem(R.drawable.slots2, "Φρουτάκια"),
            IconTextItem(R.drawable.featured, "Trending Now"),
            IconTextItem(R.drawable.play_go, "Play'n GO"),
            IconTextItem(R.drawable.playson_category, "Playson")
        )

        val secondItems = listOf(
            SecondLineItem(R.drawable.fanclub, "Novibet Club", "HOT"),
            SecondLineItem(R.drawable.giftwheel, "Δωροτροχός", "FREE"),
            SecondLineItem(R.drawable.tournaments, "Τουρνουά", "NONE"),
            SecondLineItem(R.drawable.offers, "Προσφορές", "ΝΟΝΕ"),
            SecondLineItem(R.drawable.calendar, "Ημερολόγιο", "HOT"),
            SecondLineItem(R.drawable.skillz, "Crash Παιχνίδια", "NONE"),
            SecondLineItem(R.drawable.novi_battles, "Noviμαχίες", "NONE")
        )

        val adItems = listOf(
            AdItem(R.drawable.carnival),
            AdItem(R.drawable.carousel),
            AdItem(R.drawable.carousel_gr),
            AdItem(R.drawable.carousel_2024_gr),
            AdItem(R.drawable.image1),
            AdItem(R.drawable.image2),
            AdItem(R.drawable.image3),
            AdItem(R.drawable.image4),
            AdItem(R.drawable.image5),
            AdItem(R.drawable.image6),
            AdItem(R.drawable.image7),
            AdItem(R.drawable.image8),
            AdItem(R.drawable.image9)
        )

        firstRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        firstRecyclerView.adapter = IconTextAdapter(firstItems)

        secondRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        secondRecyclerView.adapter = SecondLineAdapter(secondItems)

        viewPagerAds.adapter = AdsPagerAdapter(adItems)
        customTabLines.setTabCount(adItems.size)

        viewPagerAds.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                customTabLines.setCurrentTab(position)
                progress = 0f
            }
        })

        startAutoScroll()

        val slotGames = listOf(
            SlotGame(R.drawable.coin_toss, "Coin Toss", "CTRL-ALT-DIT", true),
            SlotGame(R.drawable.piggies, "Piggies and the Bank Cash Collect and Link", "Playtech", false),
            SlotGame(R.drawable.game2, "Carnaval Drums", "Gameburger Studios", false),
            SlotGame(R.drawable.game3, "Boss Cass Deluxe", "Alchemy Gaming", false),
            SlotGame(R.drawable.game4, "Lucky Piper", "Gamevy", false),
            SlotGame(R.drawable.game5, "Camelot Cash", "Relax Gaming", false),
            SlotGame(R.drawable.game6, "Kingfisher of the Caribbean", "Wishbone", false),
            SlotGame(R.drawable.game7, "Lucky Streak 27", "Endorphina", false),
            SlotGame(R.drawable.game8, "Hyperstrike Diamond Drums", "Gameburger Studios", false),
            SlotGame(R.drawable.game9, "Rome Fight For Gold Eternal Empire", "Foxium", false),
            SlotGame(R.drawable.game10, "Silver Lux Triple Bonus Gems", "Novomatic", false),
            SlotGame(R.drawable.game11, "Streak of Luck Double Dice", "Playtech", false),
            SlotGame(R.drawable.game12, "Deco Diamonds Elite", "Just For The Win", false)
        )

        val recyclerViewSlots: RecyclerView = findViewById(R.id.recycler_view_slots)
        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        recyclerViewSlots.layoutManager = gridLayoutManager
        recyclerViewSlots.adapter = SlotGameAdapter(slotGames)

        val gameCountTextView: TextView = findViewById(R.id.game_count)
        val gameCount = slotGames.size
        gameCountTextView.text = gameCount.toString()
    }

    private fun startAutoScroll() {
        val runnable = object : Runnable {
            override fun run() {
                if (viewPagerAds.adapter != null) {
                    progress += 0.01f
                    if (progress >= 1f) {
                        currentPage = (currentPage + 1) % (viewPagerAds.adapter!!.itemCount)
                        viewPagerAds.setCurrentItem(currentPage, true)
                        progress = 0f
                    }
                    customTabLines.setProgress(progress)
                    handler.postDelayed(this, 60) // Update progress every 30 milliseconds
                }
            }
        }
        handler.postDelayed(runnable, 30)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}