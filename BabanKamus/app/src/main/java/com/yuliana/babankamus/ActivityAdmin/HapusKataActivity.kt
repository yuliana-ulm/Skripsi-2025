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
import com.yuliana.babankamus.R

class HapusKataActivity : AppCompatActivity() {

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
    private lateinit var teksviewturunan: TextView
    private lateinit var buttonCariKata: Button
    private lateinit var buttonHapusKata: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hapus_kata)
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
        teksviewturunan = findViewById(R.id.teksviewturunan)
        buttonCariKata = findViewById(R.id.buttonCariKata)
        buttonHapusKata = findViewById(R.id.buttonHapusKata)

        setFormVisibility(false)

        buttonCariKata.setOnClickListener {
            val kata = inputKata.text.toString().trim().lowercase()
            if (kata.isNotEmpty()) {
                cariDanTampilkanData(kata)
            } else {
                Toast.makeText(this, "Silakan masukkan kata terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        buttonHapusKata.setOnClickListener {
            val kata = inputKata.text.toString().trim().lowercase()
            hapusDariFirestore(kata)
        }
    }

    private fun cariDanTampilkanData(kata: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_banjar_indonesia")
            .document(kata)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data!!
                    inputSukuKata.setText(data["sukukata"] as? String ?: "")

                    val defList = data["definisi_umum"] as? List<HashMap<*, *>>
                    defList?.firstOrNull()?.let {
                        inputDefinisi.setText(it["definisi"] as? String ?: "")
                        inputKelasKata.setText(it["kelaskata"] as? String ?: "")
                        val contoh = it["contoh"] as? List<HashMap<*, *>>
                        contoh?.firstOrNull()?.let { c ->
                            inputContohBanjar.setText(c["banjar"] as? String ?: "")
                            inputContohIndo.setText(c["indonesia"] as? String ?: "")
                        }
                    }

                    val turunanList = data["turunan"] as? List<HashMap<*, *>>
                    if (!turunanList.isNullOrEmpty()) {
                        teksviewturunan.visibility = View.VISIBLE
                        val turunan = turunanList[0]
                        inputTurunanKata.setText(turunan["kata"] as? String ?: "")
                        val defTurunan = (turunan["definisi_umum"] as? List<HashMap<*, *>>)?.get(0)
                        inputDefTurunan.setText(defTurunan?.get("definisi") as? String ?: "")
                        inputKelasTurunan.setText(defTurunan?.get("kelaskata") as? String ?: "")
                        val contohT = defTurunan?.get("contoh") as? List<HashMap<*, *>>
                        contohT?.firstOrNull()?.let {
                            inputContohTurunanBanjar.setText(it["banjar"] as? String ?: "")
                            inputContohTurunanIndo.setText(it["indonesia"] as? String ?: "")
                        }
                    }

                    setFormVisibility(true)
                    Toast.makeText(this, "✅ Data ditemukan", Toast.LENGTH_SHORT).show()
                } else {
                    setFormVisibility(false)
                    Toast.makeText(this, "❌ Kata tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                setFormVisibility(false)
                Toast.makeText(this, "Terjadi kesalahan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapusDariFirestore(kata: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_banjar_indonesia")
            .document(kata)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Kata berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Gagal menghapus kata", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setFormVisibility(visible: Boolean) {
        val vis = if (visible) View.VISIBLE else View.GONE

        inputSukuKata.visibility = vis
        inputDefinisi.visibility = vis
        inputKelasKata.visibility = vis
        inputContohBanjar.visibility = vis
        inputContohIndo.visibility = vis

        inputTurunanKata.visibility = vis
        inputDefTurunan.visibility = vis
        inputKelasTurunan.visibility = vis
        inputContohTurunanBanjar.visibility = vis
        inputContohTurunanIndo.visibility = vis
        teksviewturunan.visibility = vis
        buttonHapusKata.visibility = vis
    }
}