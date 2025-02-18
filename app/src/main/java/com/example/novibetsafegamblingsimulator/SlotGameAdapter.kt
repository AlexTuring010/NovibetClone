package com.example.novibetsafegamblingsimulator

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView

data class SlotGame(
    val imageResId: Int,
    val gameName: String,
    val studioName: String,
    val isClickable: Boolean 
)

class SlotGameAdapter(private val slotGames: List<SlotGame>) : RecyclerView.Adapter<SlotGameAdapter.SlotGameViewHolder>() {

    class SlotGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageSlotGame: ImageView = itemView.findViewById(R.id.image_slot_game)
        val textGameName: TextView = itemView.findViewById(R.id.text_game_name)
        val textStudioName: TextView = itemView.findViewById(R.id.text_studio_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slot_game, parent, false)
        return SlotGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotGameViewHolder, position: Int) {
        val slotGame = slotGames[position]
        holder.imageSlotGame.setImageResource(slotGame.imageResId)
        holder.textGameName.text = slotGame.gameName
        holder.textStudioName.text = slotGame.studioName
        if (slotGame.isClickable) {
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, SlotGameDetailActivity::class.java)
                intent.putExtra("gameName", slotGame.gameName)
                intent.putExtra("studioName", slotGame.studioName)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    context,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                context.startActivity(intent, options.toBundle())
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return slotGames.size
    }
}