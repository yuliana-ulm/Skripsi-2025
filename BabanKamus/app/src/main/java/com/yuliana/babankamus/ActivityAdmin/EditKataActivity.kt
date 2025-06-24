package com.yuliana.babankamus.ActivityAdmin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

class EditKataActivity : AppCompatActivity() {

    private lateinit var inputKata: EditText
    private lateinit var inputSukuKata: EditText
    private lateinit var inputDefinisi: EditText
    private lateinit var inputKelasKata: EditText
    private lateinit var inputContohBanjar: EditText
    private lateinit var inputContohIndo: EditText
    private lateinit var teksviewturunan: TextView
    private lateinit var viewturunan: View
    private lateinit var inputTurunanKata: EditText
    private lateinit var inputDefTurunan: EditText
    private lateinit var inputKelasTurunan: EditText
    private lateinit var inputContohTurunanBanjar: EditText
    private lateinit var inputContohTurunanIndo: EditText
    private lateinit var buttonUpdateKata: Button
    private lateinit var buttonCariKata: Button

    private fun setFormVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) android.view.View.VISIBLE else android.view.View.GONE

        inputSukuKata.visibility = visibility
        inputDefinisi.visibility = visibility
        inputKelasKata.visibility = visibility
        inputContohBanjar.visibility = visibility
        inputContohIndo.visibility = visibility

        teksviewturunan.visibility = visibility
        viewturunan.visibility = visibility
        inputTurunanKata.visibility = visibility
        inputDefTurunan.visibility = visibility
        inputKelasTurunan.visibility = visibility
        inputContohTurunanBanjar.visibility = visibility
        inputContohTurunanIndo.visibility = visibility

        buttonUpdateKata.visibility = visibility
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_kata)
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

        teksviewturunan = findViewById(R.id.teksviewturunan)
        viewturunan = findViewById(R.id.viewturunan)
        inputTurunanKata = findViewById(R.id.inputTurunanKata)
        inputDefTurunan = findViewById(R.id.inputDefTurunan)
        inputKelasTurunan = findViewById(R.id.inputKelasTurunan)
        inputContohTurunanBanjar = findViewById(R.id.inputContohTurunanBanjar)
        inputContohTurunanIndo = findViewById(R.id.inputContohTurunanIndo)

        buttonUpdateKata = findViewById(R.id.buttonUpdateKata)
        buttonCariKata = findViewById(R.id.buttonCariKata)

        setFormVisibility(false)

        buttonCariKata.setOnClickListener {
            val kata = inputKata.text.toString().trim().lowercase()
            if (kata.isNotEmpty()) {
                isiDataJikaAda(kata)
            } else {
                Toast.makeText(this, "Silakan masukkan kata dulu", Toast.LENGTH_SHORT).show()
            }
        }

        buttonUpdateKata.setOnClickListener {
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

            updateDataFirestore(kamusBaru)
        }
    }

    private fun isiDataJikaAda(kata: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_banjar_indonesia")
            .document(kata)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener

                    inputSukuKata.setText(data["sukukata"] as? String ?: "")

                    val definisiList = data["definisi_umum"] as? List<HashMap<*, *>>
                    if (!definisiList.isNullOrEmpty()) {
                        val d = definisiList[0]
                        inputDefinisi.setText(d["definisi"] as? String ?: "")
                        inputKelasKata.setText(d["kelaskata"] as? String ?: "")

                        val contohList = d["contoh"] as? List<HashMap<*, *>>
                        if (!contohList.isNullOrEmpty()) {
                            val c = contohList[0]
                            inputContohBanjar.setText(c["banjar"] as? String ?: "")
                            inputContohIndo.setText(c["indonesia"] as? String ?: "")
                        }
                    }

                    val turunanList = data["turunan"] as? List<HashMap<*, *>>
                    if (!turunanList.isNullOrEmpty()) {
                        val t = turunanList[0]
                        inputTurunanKata.setText(t["kata"] as? String ?: "")
                        val defTur = (t["definisi_umum"] as? List<HashMap<*, *>>)?.get(0)
                        inputDefTurunan.setText(defTur?.get("definisi") as? String ?: "")
                        inputKelasTurunan.setText(defTur?.get("kelaskata") as? String ?: "")

                        val contohTur = defTur?.get("contoh") as? List<HashMap<*, *>>
                        if (!contohTur.isNullOrEmpty()) {
                            inputContohTurunanBanjar.setText(contohTur[0]["banjar"] as? String ?: "")
                            inputContohTurunanIndo.setText(contohTur[0]["indonesia"] as? String ?: "")
                        }
                    }
                    setFormVisibility(true)
                    Toast.makeText(this, "✅ Data ditemukan dan diisi!", Toast.LENGTH_SHORT).show()
                } else {
                    setFormVisibility(false)
                    Toast.makeText(this, "❌ Kata tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                setFormVisibility(false)
                Toast.makeText(this, "Gagal mengambil data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDataFirestore(kamus: Kamus) {
        val db = FirebaseFirestore.getInstance()

        val definisiList = kamus.definisi_umum.map {
            mapOf(
                "definisi" to it.definisi,
                "kelaskata" to it.kelaskata,
                "contoh" to it.contoh.map { c -> mapOf("banjar" to c.banjar, "indonesia" to c.indonesia) }
            )
        }

        val turunanList = kamus.turunan.map {
            mapOf(
                "kata" to it.kata,
                "definisi_umum" to it.definisi_umum.map { d ->
                    mapOf(
                        "definisi" to d.definisi,
                        "kelaskata" to d.kelaskata,
                        "contoh" to d.contoh.map { c -> mapOf("banjar" to c.banjar, "indonesia" to c.indonesia) }
                    )
                }
            )
        }

        val data = mapOf(
            "kata" to kamus.kata,
            "sukukata" to kamus.sukukata,
            "definisi_umum" to definisiList,
            "turunan" to turunanList
        )

        db.collection("kamus_banjar_indonesia")
            .document(kamus.kata.lowercase())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
    }
}