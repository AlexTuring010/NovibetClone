package com.example.novibetsafegamblingsimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BetResultAdapter(private val betResults: MutableList<BetResult>) :
    RecyclerView.Adapter<BetResultAdapter.BetResultViewHolder>() {

    class BetResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTextView: TextView = itemView.findViewById(R.id.result_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bet_result, parent, false)
        return BetResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: BetResultViewHolder, position: Int) {
        val betResult = betResults[position]
        val amountText = if (betResult.isWin) "+${betResult.amount.toInt()}€" else "-${betResult.amount.toInt()}€"
        holder.resultTextView.text = "${betResult.result} $amountText"
        holder.resultTextView.setTextColor(
            if (betResult.isWin) holder.itemView.context.getColor(R.color.green)
            else holder.itemView.context.getColor(R.color.red)
        )
    }

    override fun getItemCount(): Int {
        return betResults.size
    }

    fun addBetResult(betResult: BetResult) {
        betResults.add(betResult)
        notifyItemInserted(betResults.size - 1)
    }
}

data class BetResult(val result: String, val isWin: Boolean, val amount: Float)
