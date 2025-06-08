package com.yuliana.bahasabanjar.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

object KamusLoader {

    fun <KamusEntry> loadAllEntriesAsDataClass(context: Context): List<KamusEntry> {
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
                            val entry = parseJsonToKamusEntry<Any>(jsonObject)
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

    fun <KamusEntry> parseJsonToKamusEntry(json: JSONObject): KamusEntry {
        fun parseContoh(array: JSONArray): List<Contoh> {
            val list = mutableListOf<Contoh>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Contoh(
                        banjar = obj.optString("banjar"),
                        indonesia = obj.optString("indonesia")
                    )
                )
            }
            return list
        }

        fun parseDefinisi(array: JSONArray): List<Definisi> {
            val list = mutableListOf<Definisi>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Definisi(
                        definisi = obj.optString("definisi"),
                        kelaskata = obj.optString("kelaskata"),
                        suara = obj.optString("suara"),
                        contoh = parseContoh(obj.optJSONArray("contoh") ?: JSONArray())
                    )
                )
            }
            return list
        }

        fun parseTurunan(array: JSONArray): List<Turunan> {
            val list = mutableListOf<Turunan>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Turunan(
                        kata = obj.optString("kata"),
                        sukukata = obj.optString("sukukata"),
                        gambar = obj.optString("gambar"),
                        definisi_umum = parseDefinisi(obj.optJSONArray("definisi_umum") ?: JSONArray())
                    )
                )
            }
            return list
        }

    fun uploadSemuaKamusKeFirestore(context: Context) {
        val entries = loadAllEntriesAsDataClass<Any?>(context)
        val firestore = FirebaseFirestore.getInstance()

        for (entry in entries) {
            val abjad = entry.kata.firstOrNull()?.lowercaseChar()?.toString() ?: ""
            val finalEntry = entry.copy(abjad = abjad)

            firestore.collection("Kamus")
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
