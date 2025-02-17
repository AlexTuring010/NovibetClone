package com.example.novibetsafegamblingsimulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstRecyclerView = findViewById<RecyclerView>(R.id.first_recycler_view)
        val secondRecyclerView = findViewById<RecyclerView>(R.id.second_recycler_view)

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

        firstRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        firstRecyclerView.adapter = IconTextAdapter(firstItems)

        secondRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        secondRecyclerView.adapter = SecondLineAdapter(secondItems)
    }
}