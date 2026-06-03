package com.example.hw1

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Saves and loads the top-10 high scores using SharedPreferences.
 * Scores are serialized as a JSON array.
 */
object ScoreManager {

    private const val PREF_NAME = "escape_tyson2_scores"
    private const val KEY_SCORES = "scores_json"
    private const val MAX_ENTRIES = 10

    private val gson = Gson()

    /** Adds [record] to the leaderboard and keeps only the top 10. */
    fun saveScore(context: Context, record: ScoreRecord) {
        val list = getScores(context).toMutableList()
        list.add(record)
        list.sortWith(compareByDescending<ScoreRecord> { it.score }.thenByDescending { it.coins })
        val topList = list.take(MAX_ENTRIES)
        val json = gson.toJson(topList)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SCORES, json)
            .apply()
    }

    /** Returns all saved scores, sorted by score descending. */
    fun getScores(context: Context): List<ScoreRecord> {
        val json = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<ScoreRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}