package com.yuliana.bahasabanjar.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    fun tambahKata(data: Map<String, Any>) {
        db.collection("Kamus")
            .add(data)
            .addOnSuccessListener { Log.d("Firestore", "Berhasil tambah kata") }
            .addOnFailureListener { e -> Log.e("Firestore", "Gagal", e) }
    }

    fun ambilSemuaKata(callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("Kamus")
            .get()
            .addOnSuccessListener { result ->
                val hasil = result.map { it.data }
                callback(hasil)
            }
    }

    fun cariKata(kata: String, callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("Kamus")
            .whereEqualTo("kata", kata.lowercase())
            .get()
            .addOnSuccessListener { result ->
                val hasil = result.map { it.data }
                callback(hasil)
            }
    }
}