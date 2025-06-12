package com.yuliana.babankamus.ActivityAdmin

import com.yuliana.babankamus.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class UploadTurunan : AppCompatActivity() {

    private lateinit var editTextKata: EditText
    private lateinit var editTextAbjad: EditText
    private lateinit var editTextDefinisi: EditText
    private lateinit var editTextSuku: EditText
    private lateinit var editTextIdGambar: EditText
    private lateinit var editTextIdSuara: EditText
    private lateinit var btnSimpan: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_turunan)

        editTextKata = findViewById(R.id.editTextKata)
        editTextAbjad = findViewById(R.id.editTextAbjad)
        editTextDefinisi = findViewById(R.id.editTextDefinisi)
        editTextSuku = findViewById(R.id.editTextSuku)
        editTextIdGambar = findViewById(R.id.editTextIdGambar)
        editTextIdSuara = findViewById(R.id.editTextIdSuara)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val kata = editTextKata.text.toString().trim()
            val abjad = editTextAbjad.text.toString().trim()
            val definisiList = editTextDefinisi.text.toString().split(",").map { it.trim() }
            val suku = editTextSuku.text.toString().trim()
            val idGambar = editTextIdGambar.text.toString().trim()
            val idSuara = editTextIdSuara.text.toString().trim()

            if (kata.isEmpty() || abjad.isEmpty()) {
                Toast.makeText(this, "Kata dan abjad tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val refGambar: DocumentReference? = if (idGambar.isNotEmpty()) {
                firestore.collection("kamus_gambar").document(idGambar)
            } else null

            val refSuara: DocumentReference? = if (idSuara.isNotEmpty()) {
                firestore.collection("kamus_suara").document(idSuara)
            } else null

            val data = mutableMapOf<String, Any>(
                "abjad" to abjad,
                "definisi" to definisiList,
                "suku" to suku
            )

            refGambar?.let { data["definisi"] = definisiList + listOf(it) }
            refSuara?.let { data["definisi"] = (data["definisi"] as List<Any>) + listOf(it) }

            firestore.collection("kamus").document(kata)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
