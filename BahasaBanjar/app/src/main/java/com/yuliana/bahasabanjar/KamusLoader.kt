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
            val entry = jsonObject.getJSONObject(key)

            // Tambahkan arti kata dasar
            if (entry.has("arti")) {
                kamus[key.lowercase()] = entry.getString("arti")
            }

            // Tambahkan variasi kata (jika ada)
            if (entry.has("variasi")) {
                val variasiObj = entry.getJSONObject("variasi")
                val variasiKeys = variasiObj.keys()
                while (variasiKeys.hasNext()) {
                    val varKey = variasiKeys.next()
                    val artiVar = variasiObj.getString(varKey)
                    kamus[varKey.lowercase()] = artiVar
                }
            }
        }

        return kamus
    }
}
