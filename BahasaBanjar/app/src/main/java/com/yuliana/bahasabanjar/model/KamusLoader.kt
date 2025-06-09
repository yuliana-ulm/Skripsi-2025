package com.yuliana.bahasabanjar.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

object KamusLoader {

    inline fun <reified KamusEntry> loadAllEntriesAsDataClass(context: Context): List<KamusEntry> {
        val result = mutableListOf<KamusEntry>()

        try {
            val files = context.assets.list("") ?: return result

            for (file in files) {
                if (file.matches(Regex("[a-z]\\.json"))) {
                    try {
                        val jsonText = context.assets.open(file)
                            .bufferedReader().use { it.readText() }

                        val jsonArray = JSONArray(jsonText)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val entry = parseJsonToKamusEntry<KamusEntry>(jsonObject)
                            result.add(entry)
                        }
                    } catch (e: Exception) {
                        Log.e("KamusLoader", "Gagal parsing file: $file", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("KamusLoader", "Gagal akses assets", e)
        }

        return result
    }

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

            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(jsonArray.toString().toByteArray())
            }

            Toast.makeText(context, "Backup selesai", Toast.LENGTH_SHORT).show()
        }
    }

    inline fun <reified KamusEntry> parseJsonToKamusEntry(json: JSONObject): KamusEntry {
        val contohParser: (JSONArray) -> List<Contoh> = { array ->
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Contoh(obj.optString("banjar"), obj.optString("indonesia"))
            }
        }

        val definisiParser: (JSONArray) -> List<Definisi> = { array ->
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Definisi(
                    obj.optString("definisi"),
                    obj.optString("kelaskata"),
                    obj.optString("suara"),
                    contohParser(obj.optJSONArray("contoh") ?: JSONArray())
                )
            }
        }

        val turunanParser: (JSONArray) -> List<Turunan> = { array ->
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Turunan(
                    obj.optString("kata"),
                    obj.optString("sukukata"),
                    obj.optString("gambar"),
                    definisiParser(obj.optJSONArray("definisi_umum") ?: JSONArray())
                )
            }
        }

        val entry = KamusEntry(
            kata = json.optString("kata"),
            sukukata = json.optString("sukukata"),
            gambar = json.optString("gambar"),
            abjad = json.optString("abjad"),
            definisi_umum = definisiParser(json.optJSONArray("definisi_umum") ?: JSONArray()),
            turunan = turunanParser(json.optJSONArray("turunan") ?: JSONArray())
        )

        @Suppress("UNCHECKED_CAST")
        return entry as KamusEntry
    }

    fun uploadSemuaKamusKeFirestore(context: Context) {
        val entries = loadAllEntriesAsDataClass<KamusEntry>(context)
        val firestore = FirebaseFirestore.getInstance()

        for (entry in entries) {
            val abjad = entry.kata.firstOrNull()?.lowercaseChar()?.toString() ?: ""
            val finalEntry = entry.copy(abjad = abjad)

            firestore.collection("kamus_banjar_indonesia")
                .document(finalEntry.kata)
                .set(finalEntry)
                .addOnSuccessListener {
                    Log.d("KamusUploader", "Berhasil upload: ${finalEntry.kata}")
                }
                .addOnFailureListener { e ->
                    Log.e("KamusUploader", "Gagal upload: ${finalEntry.kata}", e)
                }
        }
    }
}
