package com.yuliana.bahasabanjar.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

object Util {

    fun importJsonToFirestore(context: Context, fileName: String = "abjad.json") {
        val db = FirebaseFirestore.getInstance()

        try {
            val inputStream = context.assets.open(fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val iterator = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                val item = jsonObject.getJSONObject(key)

                val data = hashMapOf(
                    "kata" to item.getString("kata"),
                    "suku_kata" to item.getString("suku_kata"),
                    "kelas_kata" to item.getString("kelas_kata"),
                    "arti" to item.getString("arti"),
                    "contoh" to item.getString("contoh")
                )

                db.collection("kamus")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("FirestoreUtil", "Berhasil upload: $key")
                    }
                    .addOnFailureListener {
                        Log.e("FirestoreUtil", "Gagal upload: $key", it)
                    }
            }

            Toast.makeText(context, "Import data berhasil!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membaca file JSON", Toast.LENGTH_SHORT).show()
        }
    }
}
