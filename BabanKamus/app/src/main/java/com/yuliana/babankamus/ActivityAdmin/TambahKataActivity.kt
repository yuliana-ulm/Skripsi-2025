package com.yuliana.babankamus.ActivityAdmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.Model.Contoh
import com.yuliana.babankamus.Model.Definisi
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.Model.Turunan
import com.yuliana.babankamus.R

class TambahKataActivity : AppCompatActivity() {

    private lateinit var inputKata: EditText
    private lateinit var inputSukuKata: EditText
    private lateinit var inputDefinisi: EditText
    private lateinit var inputKelasKata: EditText
    private lateinit var inputContohBanjar: EditText
    private lateinit var inputContohIndo: EditText
    private lateinit var inputTurunanKata: EditText
    private lateinit var inputDefTurunan: EditText
    private lateinit var inputKelasTurunan: EditText
    private lateinit var inputContohTurunanBanjar: EditText
    private lateinit var inputContohTurunanIndo: EditText
    private lateinit var buttonSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_kata)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tombolKembali = findViewById<ImageButton>(R.id.buttonKembali)
        tombolKembali.setOnClickListener {
            val intent = Intent(this, DashAdmin::class.java)
            startActivity(intent)
            finish()
        }

        inputKata = findViewById(R.id.inputKata)
        inputSukuKata = findViewById(R.id.inputSukuKata)
        inputDefinisi = findViewById(R.id.inputDefinisi)
        inputKelasKata = findViewById(R.id.inputKelasKata)
        inputContohBanjar = findViewById(R.id.inputContohBanjar)
        inputContohIndo = findViewById(R.id.inputContohIndo)

        inputTurunanKata = findViewById(R.id.inputTurunanKata)
        inputDefTurunan = findViewById(R.id.inputDefTurunan)
        inputKelasTurunan = findViewById(R.id.inputKelasTurunan)
        inputContohTurunanBanjar = findViewById(R.id.inputContohTurunanBanjar)
        inputContohTurunanIndo = findViewById(R.id.inputContohTurunanIndo)

        //memanggil fungsi a-z taditu
        setOnlyLetterInput(
            inputKata, inputSukuKata, inputDefinisi, inputKelasKata,
            inputTurunanKata, inputDefTurunan, inputKelasTurunan
        )

        buttonSimpan = findViewById(R.id.buttonSimpanKata)
        buttonSimpan.setOnClickListener {
            val kata = inputKata.text.toString().trim()
            val definisiUtama = inputDefinisi.text.toString().trim()

            if (kata.isEmpty() || definisiUtama.isEmpty()) {
                Toast.makeText(this, "Kata dan definisi wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contohUtama = listOfNotNull(
                if (inputContohBanjar.text.toString()
                        .isNotBlank() || inputContohIndo.text.toString().isNotBlank()
                ) {
                    Contoh(
                        banjar = inputContohBanjar.text.toString().trim(),
                        indonesia = inputContohIndo.text.toString().trim()
                    )
                } else null
            )

            val definisi = Definisi(
                definisi = definisiUtama,
                kelaskata = inputKelasKata.text.toString().trim(),
                contoh = contohUtama
            )

            val turunanList = mutableListOf<Turunan>()
            val turunanKata = inputTurunanKata.text.toString().trim()
            val turunanDef = inputDefTurunan.text.toString().trim()

            if (turunanKata.isNotEmpty() && turunanDef.isNotEmpty()) {
                val contohTurunan = listOfNotNull(
                    if (inputContohTurunanBanjar.text.toString()
                            .isNotBlank() || inputContohTurunanIndo.text.toString().isNotBlank()
                    ) {
                        Contoh(
                            banjar = inputContohTurunanBanjar.text.toString().trim(),
                            indonesia = inputContohTurunanIndo.text.toString().trim()
                        )
                    } else null
                )

                val defTurunan = Definisi(
                    definisi = turunanDef,
                    kelaskata = inputKelasTurunan.text.toString().trim(),
                    contoh = contohTurunan
                )

                val turunan = Turunan(
                    kata = turunanKata,
                    definisi_umum = listOf(defTurunan)
                )

                turunanList.add(turunan)
            }

            val kamusBaru = Kamus(
                kata = kata,
                sukukata = inputSukuKata.text.toString().trim(),
                definisi_umum = listOf(definisi),
                turunan = turunanList
            )

            tambahKataKeFirestore(kamusBaru)
        }
    }

        private fun tambahKataKeFirestore(kamus: Kamus) {
        val db = FirebaseFirestore.getInstance()

        val definisiList = kamus.definisi_umum.map { definisi ->
            hashMapOf(
                "definisi" to definisi.definisi,
                "kelaskata" to definisi.kelaskata,
                "contoh" to definisi.contoh.map { c ->
                    hashMapOf(
                        "banjar" to c.banjar,
                        "indonesia" to c.indonesia
                    )
                }
            )
        }

        val turunanList = kamus.turunan.map { turunan ->
            hashMapOf(
                "kata" to turunan.kata,
                "definisi_umum" to turunan.definisi_umum.map { def ->
                    hashMapOf(
                        "definisi" to def.definisi,
                        "kelaskata" to def.kelaskata,
                        "contoh" to def.contoh.map { c ->
                            hashMapOf(
                                "banjar" to c.banjar,
                                "indonesia" to c.indonesia
                            )
                        }
                    )
                }
            )
        }

        val data = hashMapOf(
            "kata" to kamus.kata,
            "sukukata" to kamus.sukukata,
            "definisi_umum" to definisiList,
            "turunan" to turunanList
        )

        db.collection("kamus_banjar_indonesia")
            .document(kamus.kata.lowercase())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil tambah kata", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal tambah kata", Toast.LENGTH_SHORT).show()
            }
    }

    //untuk memberikan respon jika bukan a-z
    private fun setOnlyLetterInput(vararg editTexts: EditText) {
        val hurufOnlyFilter = android.text.InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("^[a-zA-Z]+$"))) {
                source
            } else {
                Toast.makeText(this, "Hanya huruf A-Z yang diperbolehkan", Toast.LENGTH_SHORT).show()
                ""
            }
        }

        editTexts.forEach { it.filters = arrayOf(hurufOnlyFilter) }
    }
}