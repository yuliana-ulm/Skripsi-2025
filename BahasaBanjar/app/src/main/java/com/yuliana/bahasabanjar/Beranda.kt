package com.yuliana.bahasabanjar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class EntriKamus(
    val arti: String,
    val variasi: Map<String, String>? = null
)

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

        // Button 1 logic
        buttonbanjarindo.setOnClickListener {
            val inputText = editText?.text?.toString()?.lowercase()?.trim() ?: ""
            val kataInput = inputText.split(" ")

            val kamus = KamusLoader.loadKamus(this)

            // DEBUG: Tampilkan semua key dalam kamus
            Log.d("DEBUG_KAMUS_KEYS", "Isi keys kamus: ${kamus.keys.joinToString()}")

            val hasilTerjemahan = kataInput.map { kata ->
                val kataBersih = kata.lowercase().trim()
                Log.d("DEBUG_CARI_KATA", "Mencari arti dari: '$kataBersih'")
                if (kamus.containsKey(kataBersih)) {
                    Log.d("DEBUG_HASIL", "Ditemukan: ${kamus[kataBersih]}")
                } else {
                    Log.d("DEBUG_HASIL", "Tidak ditemukan untuk kata: '$kataBersih'")
                }

                kamus[kataBersih] ?: "[tidak ditemukan]"
            }

            val kalimatHasil = hasilTerjemahan.joinToString(" ")
            textViewHasil.text = kalimatHasil
        }


        // Button 2 logic
        button2.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
