package com.yuliana.bahasabanjar

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Beranda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI references
        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = textInputLayout.editText
        val buttonbanjarindo = findViewById<Button>(R.id.banjarindo)
        val button2 = findViewById<Button>(R.id.button2)
        val textViewHasil = findViewById<TextView>(R.id.textViewHasil)

        val kamus = KamusLoader.loadKamus(this)

        // Button 1 logic
        buttonbanjarindo.setOnClickListener {
            val inputText = editText?.text?.toString()?.lowercase()?.trim() ?: ""
            val kataInput = inputText.split(" ")

            val hasilTerjemahan = kataInput.map { kata ->
                var hasil = "[tidak ditemukan]"
                var jarakTerdekat = Int.MAX_VALUE

                for ((banjar, indo) in kamus) {
                    val jarak = Levenshtein.calculate(kata, banjar)

                    if (jarak < jarakTerdekat) {
                        jarakTerdekat = jarak
                        hasil = indo
                    }
                }

                // Jika jarak terlalu jauh, anggap tidak valid
                if (jarakTerdekat <= 2) hasil else "[tidak ditemukan]"
            }

            val kalimatHasil = hasilTerjemahan.joinToString(" ")
            textViewHasil.text = kalimatHasil
        }


        // Button 2 logic
        button2.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadJSONFromAsset(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }

    fun cariArtiKata(
        kata: String,
        kamus: Map<String, Map<String, Any>>
    ): String {
        val kataLower = kata.lowercase()

        // Cek kata dasar langsung
        if (kamus.containsKey(kataLower)) {
            val entri = kamus[kataLower]
            return entri?.get("arti") as? String ?: "[tidak ditemukan]"
        }

        // Cek kata turunan (imbuhan)
        for ((akar, data) in kamus) {
            val variasi = data["variasi"] as? Map<*, *> ?: continue
            if (variasi.containsKey(kataLower)) {
                return variasi[kataLower] as? String ?: "[tidak ditemukan]"
            }
        }

        return "[tidak ditemukan]"
    }


}
