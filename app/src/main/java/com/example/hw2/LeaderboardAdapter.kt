package com.example.hw2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Populates the leaderboard RecyclerView.
 * Calls [onRowClick] when the user taps a row (so the map can pan to that location).
 */
class LeaderboardAdapter(
    private val scores: List<ScoreRecord>,
    private val onRowClick: (ScoreRecord) -> Unit
) : RecyclerView.Adapter<LeaderboardAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRank: TextView     = itemView.findViewById(R.id.txt_rank)
        val txtName: TextView     = itemView.findViewById(R.id.txt_name)
        val txtScore: TextView    = itemView.findViewById(R.id.txt_score)
        val txtDist: TextView     = itemView.findViewById(R.id.txt_dist)
        val txtLocation: TextView = itemView.findViewById(R.id.txt_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score_row, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = scores[position]
        holder.txtRank.text     = "#${position + 1}"
        holder.txtName.text     = record.name
        holder.txtScore.text    = "${record.score} m"
        holder.txtDist.text     = record.coins.toString()
        holder.txtLocation.text = record.locationName

        val colorRes = if (position % 2 == 0) R.color.row_even else R.color.row_odd
        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorRes))

        holder.itemView.setOnClickListener { onRowClick(record) }
    }

    override fun getItemCount() = scores.size
}