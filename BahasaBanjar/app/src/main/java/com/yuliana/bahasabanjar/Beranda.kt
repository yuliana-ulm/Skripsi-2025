package com.yuliana.bahasabanjar

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = textInputLayout.editText
        val buttonbanjarindo = findViewById<Button>(R.id.banjarindo)
        val button2 = findViewById<Button>(R.id.button2)
        val textViewHasil = findViewById<TextView>(R.id.textViewHasil)

        buttonbanjarindo.setOnClickListener {
            val inputText = editText?.text?.toString()?.lowercase()?.trim() ?: ""
            if (inputText.isNotEmpty()) {
                val semuaKamus = KamusLoader.loadAllKamusFiles(this)
                val hasil = Levenshtein.cariKataPalingDekat(inputText, semuaKamus)

                if (hasil != null) {
                    val arti = hasil.optJSONArray("meanings")?.optJSONObject(0)
                        ?.optJSONArray("definitions")?.optJSONObject(0)
                        ?.optString("definition") ?: "[definisi tidak ditemukan]"
                    textViewHasil.text = "$inputText: $arti"
                } else {
                    textViewHasil.text = "[kata tidak ditemukan]"
                }
            } else {
                textViewHasil.text = "[input kosong]"
            }
        }

        button2.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
