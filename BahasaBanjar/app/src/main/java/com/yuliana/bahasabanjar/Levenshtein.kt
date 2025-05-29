package com.yuliana.bahasabanjar

class Levenshtein {
    companion object {
        fun calculate(a: String, b: String): Int {
            val dp = Array(a.length + 1) { IntArray(b.length + 1) }

            for (i in 0..a.length) {
                for (j in 0..b.length) {
                    if (i == 0) {
                        dp[i][j] = j
                    } else if (j == 0) {
                        dp[i][j] = i
                    } else if (a[i - 1] == b[j - 1]) {
                        dp[i][j] = dp[i - 1][j - 1]
                    } else {
                        dp[i][j] = 1 + minOf(
                            dp[i - 1][j],     // Hapus
                            dp[i][j - 1],     // Tambah
                            dp[i - 1][j - 1]  // Ganti
                        )
                    }
                }
            }

            return dp[a.length][b.length]
        }
    }
}
