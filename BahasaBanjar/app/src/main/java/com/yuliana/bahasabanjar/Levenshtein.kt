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

    fun suggestWords(input: String, kamus: List<JSONObject>, maxDistance: Int = 2): List<String> {
        val suggestions = mutableListOf<Pair<String, Int>>()

        for (entry in kamus) {
            val word = entry.optString("word", "")
            val distance = distance(input, word)
            if (distance <= maxDistance) {
                suggestions.add(word to distance)
            }

            val derivatives = entry.optJSONArray("derivatives") ?: continue
            for (i in 0 until derivatives.length()) {
                val turunan = derivatives.getJSONObject(i)
                val derivedWord = turunan.optString("word", "")
                val derivedDistance = distance(input, derivedWord)
                if (derivedDistance <= maxDistance) {
                    suggestions.add(derivedWord to derivedDistance)
                }
            }
        }

        return suggestions
            .sortedBy { it.second }
            .map { it.first }
            .distinct()
    }

}
