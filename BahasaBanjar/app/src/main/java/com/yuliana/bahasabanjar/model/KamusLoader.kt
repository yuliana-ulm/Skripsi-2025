package com.yuliana.bahasabanjar.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray
import org.json.JSONObject

object KamusLoader {
    fun loadAllEntries(context: Context): List<JSONObject> {
        val result = mutableListOf<JSONObject>()

        try {
            val files = context.assets.list("") ?: return result

            for (file in files) {
                if (file.matches(Regex("[a-z]\\.json"))) {
                    try {
                        val jsonText = context.assets.open(file)
                            .bufferedReader().use { it.readText() }

                        val jsonArray = JSONArray(jsonText)
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            result.add(item)
                        }
                    } catch (e: Exception) {
                        Log.e("KamusLoader", "Gagal baca/parsing file: $file", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("KamusLoader", "Gagal baca daftar file di assets", e)
        }

        return result
    }

    fun simpanFirebaseKeJsonLokal(context: Context, fileName: String = "backup_kamus.json") {
        val dbRef = FirebaseDatabase.getInstance().getReference("kamus")
        dbRef.get().addOnSuccessListener { snapshot ->
            val jsonArray = JSONArray()
            for (child in snapshot.children) {
                val jsonString = JSONObject(child.value as Map<*, *>).toString()
                jsonArray.put(JSONObject(jsonString))
            }

            // Simpan ke file internal
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(jsonArray.toString().toByteArray())
            }

            Toast.makeText(context, "Backup selesai", Toast.LENGTH_SHORT).show()
        }
    }
}