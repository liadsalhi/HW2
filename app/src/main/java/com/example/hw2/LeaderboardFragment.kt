package com.example.hw2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Fragment 1 – leaderboard table.
 * Displays the top-10 scores in a RecyclerView.
 * When the user taps a row, it notifies the host activity via [OnScoreSelectedListener].
 */
class LeaderboardFragment : Fragment() {

    /** Implemented by HighScoresActivity to forward row taps to the MapFragment. */
    interface OnScoreSelectedListener {
        fun onScoreSelected(record: ScoreRecord)
    }

    private var listener: OnScoreSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnScoreSelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_leaderboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scores = ScoreManager.getScores(requireContext())
        val emptyMsg: TextView = view.findViewById(R.id.txt_empty)
        val recycler: RecyclerView = view.findViewById(R.id.recycler_scores)

        if (scores.isEmpty()) {
            emptyMsg.visibility  = View.VISIBLE
            recycler.visibility  = View.GONE
            return
        }

        emptyMsg.visibility = View.GONE
        recycler.visibility = View.VISIBLE
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recycler.adapter = LeaderboardAdapter(scores) { record ->
            listener?.onScoreSelected(record)
        }
    }
}