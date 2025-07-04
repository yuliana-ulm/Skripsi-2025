package com.yuliana.babankamus.ActivityAdmin

import com.yuliana.babankamus.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class UploadTurunan : AppCompatActivity() {

    private lateinit var layoutDefinisiContainer: LinearLayout
    private lateinit var btnTambahDefinisi: Button
    private lateinit var editTextKata: EditText
    private lateinit var editTextAbjad: EditText
    private lateinit var editTextSuku: EditText
    private lateinit var btnSimpan: Button

    private val daftarDefinisi = mutableListOf<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_turunan)

        layoutDefinisiContainer = findViewById(R.id.layoutDefinisiContainer)
        btnTambahDefinisi = findViewById(R.id.btnTambahDefinisi)
        editTextKata = findViewById(R.id.editTextKata)
        editTextAbjad = findViewById(R.id.editTextAbjad)
        editTextSuku = findViewById(R.id.editTextSuku)
        btnSimpan = findViewById(R.id.btnSimpan)

        // Kosongkan container dan sembunyikan awalnya
        layoutDefinisiContainer.visibility = View.GONE

        btnTambahDefinisi.setOnClickListener {
            if (layoutDefinisiContainer.visibility == View.GONE) {
                layoutDefinisiContainer.visibility = View.VISIBLE
            } else {
                tambahFormDefinisi()
            }
        }

        btnSimpan.setOnClickListener {
            simpanData()
        }
    }

    private fun tambahFormDefinisi() {
        val inflater = LayoutInflater.from(this)
        val definisiView = inflater.inflate(R.layout.item_definisi, null)
        layoutDefinisiContainer.addView(definisiView)
    }

    private fun simpanData() {
        val kata = editTextKata.text.toString().trim()
        val abjad = editTextAbjad.text.toString().trim()
        val suku = editTextSuku.text.toString().trim()

        val daftarDefinisi = mutableListOf<Map<String, String>>()

        for (i in 0 until layoutDefinisiContainer.childCount) {
            val view = layoutDefinisiContainer.getChildAt(i)

            val arti = view.findViewById<EditText>(R.id.editArti)?.text?.toString()?.trim() ?: ""
            val dialek = view.findViewById<EditText>(R.id.editDialek)?.text?.toString()?.trim() ?: ""
            val kelas = view.findViewById<EditText>(R.id.editKelasKata)?.text?.toString()?.trim() ?: ""
            val contohBanjar = view.findViewById<EditText>(R.id.editContohBanjar)?.text?.toString()?.trim() ?: ""
            val contohIndo = view.findViewById<EditText>(R.id.editContohIndo)?.text?.toString()?.trim() ?: ""
            val suara = view.findViewById<EditText>(R.id.editSuara)?.text?.toString()?.trim() ?: ""
            val gambar = view.findViewById<EditText>(R.id.editGambar)?.text?.toString()?.trim() ?: ""

            // Tambahkan ke list jika minimal satu dari semua field ada isinya
            if (arti.isNotEmpty() || dialek.isNotEmpty() || kelas.isNotEmpty() ||
                contohBanjar.isNotEmpty() || contohIndo.isNotEmpty() ||
                suara.isNotEmpty() || gambar.isNotEmpty()
            ) {
                val definisi = hashMapOf(
                    "arti" to arti,
                    "dialek" to dialek,
                    "kelas_kata" to kelas,
                    "contoh_banjar" to contohBanjar,
                    "contoh_indonesia" to contohIndo,
                    "suara" to suara,
                    "gambar" to gambar
                )
                daftarDefinisi.add(definisi)
            }
        }

        val dataKata = hashMapOf(
            "kata" to kata,
            "abjad" to abjad,
            "suku" to suku,
            "definisi_umum" to daftarDefinisi
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("kamus")
            .add(kata)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan ke Firestore 💾", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}