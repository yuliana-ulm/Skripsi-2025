package com.yuliana.bahasabanjar

import android.content.Context
import org.json.JSONObject

object KamusLoader {
    fun loadKamus(context: Context): Map<String, String> {
        val inputStream = context.assets.open("Kamus.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val jsonObject = JSONObject(jsonString)
        val kamus = mutableMapOf<String, String>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.getString(key)
            kamus[key] = value
        }

        return kamus
    }
}
