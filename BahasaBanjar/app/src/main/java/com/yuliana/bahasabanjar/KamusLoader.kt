package com.yuliana.bahasabanjar

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object KamusLoader {
    fun loadAllEntries(context: Context): List<JSONObject> {
        val result = mutableListOf<JSONObject>()
        val files = context.assets.list("") ?: return result

        for (file in files) {
            if (file.matches(Regex("[a-z]\\.json"))) {
                val jsonText = context.assets.open(file).bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonText)
                for (i in 0 until jsonArray.length()) {
                    result.add(jsonArray.getJSONObject(i))
                }
            }
        }

        return result
    }
}
