package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPagerAds: ViewPager2
    private lateinit var customTabLines: CustomTabLinesView
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var progress = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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