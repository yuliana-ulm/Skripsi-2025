package com.yuliana.bahasabanjar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject

class Beranda : AppCompatActivity() {

    private lateinit var semuaKamus: List<org.json.JSONObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText = findViewById<AutoCompleteTextView>(R.id.editText)
        val buttonbanjarindo = findViewById<Button>(R.id.banjarindo)
        val button2 = findViewById<Button>(R.id.button2)
        val textViewHasil = findViewById<TextView>(R.id.textViewHasil)

        // Load kamus hanya sekali
        semuaKamus = KamusLoader.loadAllEntries(this)

        // Adapter untuk dropdown suggestion
        val suggestionAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        editText.setAdapter(suggestionAdapter)

        // Saat mengetik, tampilkan saran di dropdown
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().lowercase().trim()
                if (input.length >= 2) {
                    val saran = Levenshtein.suggestWords(input, semuaKamus)
                    suggestionAdapter.clear()
                    suggestionAdapter.addAll(saran)
                    suggestionAdapter.notifyDataSetChanged()
                    editText.showDropDown()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Klik suggestion dari dropdown → tampilkan arti
        editText.setOnItemClickListener { _, _, position, _ ->
            val selectedWord = suggestionAdapter.getItem(position) ?: return@setOnItemClickListener
            val hasil = cariKataTepat(selectedWord, semuaKamus)
            if (hasil != null) {
                val teks = formatHasilTerjemahan(hasil)
                textViewHasil.text = teks
            } else {
                textViewHasil.text = "[kata tidak ditemukan]"
            }

        }

        // Tombol terjemahan (hanya jika cocok persis)
        buttonbanjarindo.setOnClickListener {
            val inputText = editText.text.toString().lowercase().trim()
            val hasil = cariKataTepat(inputText, semuaKamus)
            if (hasil != null) {
                val teks = formatHasilTerjemahan(hasil)
                textViewHasil.text = teks
            } else {
                textViewHasil.text = "[kata tidak ditemukan]"
            }

        }

        button2.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatHasilTerjemahan(entry: JSONObject): String {
        val word = entry.optString("word", "[?]").replaceFirstChar { it.uppercase() }
        val meanings = entry.optJSONArray("meanings") ?: return "$word: [arti tidak ditemukan]"

        val hasil = StringBuilder()
        hasil.append("$word:\n")

        for (i in 0 until meanings.length()) {
            val meaning = meanings.optJSONObject(i)
            val definitions = meaning?.optJSONArray("definitions") ?: continue

            for (j in 0 until definitions.length()) {
                val def = definitions.optJSONObject(j)
                val arti = def?.optString("definition") ?: continue
                val jenis = def.optString("partOfSpeech", "")
                hasil.append("- ($jenis) $arti\n")

                val examples = def.optJSONArray("examples")
                if (examples != null && examples.length() > 0) {
                    for (k in 0 until examples.length()) {
                        val ex = examples.optJSONObject(k)
                        val bjn = ex.optString("bjn", "")
                        val id = ex.optString("id", "")
                        if (bjn.isNotEmpty() && id.isNotEmpty()) {
                            hasil.append("   Contoh: $bjn\n")
                            hasil.append("           $id\n")
                        }
                    }
                }
            }
        }

        return hasil.toString().trim()
    }

    private fun cariKataTepat(input: String, kamus: List<JSONObject>): JSONObject? {
        for (entry in kamus) {
            val kataUtama = entry.optString("word", "").lowercase()
            if (kataUtama == input) return entry

            val derivatives = entry.optJSONArray("derivatives") ?: continue
            for (i in 0 until derivatives.length()) {
                val turunan = derivatives.getJSONObject(i)
                val turunanWord = turunan.optString("word", "").lowercase()
                if (turunanWord == input) {
                    // Bungkus turunan menjadi struktur seperti entri utama
                    val wrapped = JSONObject()
                    wrapped.put("word", turunanWord)
                    wrapped.put("meanings", JSONArray().apply {
                        put(JSONObject().apply {
                            put("definitions", turunan.optJSONArray("definitions"))
                        })
                    })
                    return wrapped
                }
            }
        }
        return null
    }
}

