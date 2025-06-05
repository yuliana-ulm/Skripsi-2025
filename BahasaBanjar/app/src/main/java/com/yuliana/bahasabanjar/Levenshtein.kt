package com.yuliana.bahasabanjar

import org.json.JSONObject

object Levenshtein {
    fun distance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost)
            }
        }

        return dp[a.length][b.length]
    }

    fun suggestdasar(input: String, kamus: List<JSONObject>, maxDistance: Int = 2): List<String> {
        val suggestions = mutableListOf<Pair<String, Int>>()

        for (entry in kamus) {
            val dasar = entry.optString("dasar", "")
            val distance = distance(input, dasar)
            if (distance <= maxDistance) {
                suggestions.add(dasar to distance)
            }

            val turunan = entry.optJSONArray("turunan") ?: continue
            for (i in 0 until turunan.length()) {
                val turunan = turunan.getJSONObject(i)
                val turunandasar = turunan.optString("dasar", "")
                val turunanDistance = distance(input, turunandasar)
                if (turunanDistance <= maxDistance) {
                    suggestions.add(turunandasar to turunanDistance)
                }
            }
        }

        return suggestions
            .sortedBy { it.second }
            .map { it.first }
            .distinct()
    }

}
