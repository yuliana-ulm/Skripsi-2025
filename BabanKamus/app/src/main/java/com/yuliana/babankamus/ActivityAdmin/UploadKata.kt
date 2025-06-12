package com.yuliana.babankamus.ActivityAdmin

import com.yuliana.babankamus.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UploadKata: AppCompatActivity() {

    private lateinit var editTextKata: EditText
    private lateinit var editTextArti: EditText
    private lateinit var editTextDefinisi: EditText
    private lateinit var btnSimpanKata: Button
    private lateinit var btnSimpanTurunan: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_kata)

        editTextKata = findViewById(R.id.editTextKata)
        editTextArti = findViewById(R.id.editTextArti)
        editTextDefinisi = findViewById(R.id.editTextDefinisi)
        btnSimpanKata = findViewById(R.id.btnSimpanKata)
        btnSimpanTurunan = findViewById(R.id.btnSimpanTurunan)

        btnSimpanKata.setOnClickListener {
            val kata = editTextKata.text.toString().trim()
            val arti = editTextArti.text.toString().trim()
            val definisi = editTextDefinisi.text.toString().split(",").map { it.trim() }

            if (kata.isEmpty() || arti.isEmpty() || definisi.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            } else {
                val data = hashMapOf(
                    "arti" to arti,
                    "definisi" to definisi
                )
                firestore.collection("kamus").document(kata)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Kata berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menyimpan kata", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnSimpanTurunan.setOnClickListener {
            val kata = editTextKata.text.toString().trim()
            val arti = editTextArti.text.toString().trim()
            val definisi = editTextDefinisi.text.toString().split(",").map { it.trim() }

            if (kata.isEmpty() || arti.isEmpty() || definisi.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data turunan", Toast.LENGTH_SHORT).show()
            } else {
                val data = hashMapOf(
                    "arti" to arti,
                    "definisi" to definisi
                )
                firestore.collection("kamus").document(kata)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Turunan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menyimpan turunan", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
