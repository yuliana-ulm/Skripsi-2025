package com.yuliana.bahasabanjar

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object KamusLoader {
    fun loadAllKamusFiles(context: Context): List<JSONObject> {
        val kamusList = mutableListOf<JSONObject>()
        val files = context.assets.list("") ?: return kamusList

        for (filename in files) {
            if (filename.matches(Regex("[a-z]\\.json"))) {
                val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    kamusList.add(jsonArray.getJSONObject(i))
                }
            }
        }
        return kamusList
    }
}
