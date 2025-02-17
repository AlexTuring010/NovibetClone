package com.example.novibetsafegamblingsimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class SecondLineItem(val imageResId: Int, val text: String, val label: String?)

class SecondLineAdapter(private val items: List<SecondLineItem>) : RecyclerView.Adapter<SecondLineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = view.findViewById(R.id.item_text)
        val hot: TextView = view.findViewById(R.id.hot_label)
        val free: TextView = view.findViewById(R.id.free_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.second_line_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageResId)
        holder.text.text = item.text
        if(item.label == "HOT"){
            holder.hot.visibility = View.VISIBLE
        } else if(item.label == "FREE"){
            holder.free.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = items.size
}