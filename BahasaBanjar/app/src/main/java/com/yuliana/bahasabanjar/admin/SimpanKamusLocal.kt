package com.yuliana.bahasabanjar.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.bahasabanjar.R
import android.os.Environment
import android.widget.*
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter


class SimpanKamusLocal<View> : AppCompatActivity() {

    private lateinit var layoutTurunanContainer: LinearLayout
    private val listViewTurunan = mutableListOf<View>()

    data class Contoh(val banjar: String, val indonesia: String)
    data class Definisi(
        val definisi: String,
        val kelaskata: String,
        val contoh: List<Contoh>,
        val suara: String
    )
    data class Turunan(
        val kata: String,
        val sukukata: String,
        val gambar: String,
        val definisi_umum: List<Definisi>
    )
    data class KamusEntry(
        val kata: String,
        val sukukata: String,
        val gambar: String,
        val definisi_umum: List<Definisi>,
        val turunan: List<Turunan>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simpan_kamus_local)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etKata = findViewById<EditText>(R.id.etKata)
        val etSukuKata = findViewById<EditText>(R.id.etSukuKata)
        val etGambar = findViewById<EditText>(R.id.etGambar)
        val etDefinisi = findViewById<EditText>(R.id.etDefinisi)
        val etKelasKata = findViewById<EditText>(R.id.etKelasKata)
        val etContohBanjar = findViewById<EditText>(R.id.etContohBanjar)
        val etContohIndo = findViewById<EditText>(R.id.etContohIndo)
        val etSuara = findViewById<EditText>(R.id.etSuara)

        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val entry = KamusEntry(
                kata = etKata.text.toString(),
                sukukata = etSukuKata.text.toString(),
                gambar = etGambar.text.toString(),
                definisi_umum = listOf(
                    Definisi(
                        definisi = etDefinisi.text.toString(),
                        kelaskata = etKelasKata.text.toString(),
                        contoh = listOf(
                            Contoh(
                                banjar = etContohBanjar.text.toString(),
                                indonesia = etContohIndo.text.toString()
                            )
                        ),
                        suara = etSuara.text.toString()
                    )
                ),
                turunan = listOf() // Belum input turunan, bisa dikembangkan
            )

            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(listOf(entry))

            try {
                val file = File(getExternalFilesDir(null), "kamus_banjar.json")
                val writer = FileWriter(file, false)
                writer.use {
                    it.write(json)
                }
                Toast.makeText(this, "Disimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal simpan JSON: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        layoutTurunanContainer = findViewById(R.id.layoutTurunanContainer)
        val btnTambahTurunan = findViewById<Button>(R.id.btnTambahTurunan)

        btnTambahTurunan.setOnClickListener {
            val turunanView = layoutInflater.inflate(R.layout.tambah_turunan, null)
            layoutTurunanContainer.addView(turunanView)
//            listViewTurunan.add(turunanView)
        }
    }
}