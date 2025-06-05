package com.yuliana.bahasabanjar

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import android.speech.tts.TextToSpeech
import android.view.View
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class Beranda : AppCompatActivity() {

    private lateinit var semuaKamus: List<JSONObject>
    private lateinit var editText: AutoCompleteTextView
    private lateinit var buttonbanjarindo: Button
    private lateinit var btnPlay: ImageButton
    private val REQ_CODE_SPEECH_INPUT = 100
    private lateinit var tts: TextToSpeech
    private var teksHasil: String = ""
    private lateinit var themeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editText = findViewById(R.id.editText)
        buttonbanjarindo = findViewById(R.id.banjarindo)
        themeSwitch = findViewById(R.id.themeSwitch)
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
                    val saran = Levenshtein.suggestdasar(input, semuaKamus)
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
                teksHasil = formatHasilTerjemahan(hasil)
                textViewHasil.text = teksHasil
                btnPlay.visibility = View.VISIBLE
                tts.speak(teksHasil, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                textViewHasil.text = "[kata tidak ditemukan]"
                btnPlay.visibility = View.GONE
            }

        }

        // Tombol terjemahan (hanya jika cocok persis)
        buttonbanjarindo.setOnClickListener {
            val inputText = editText.text.toString().lowercase().trim()
            val hasil = cariKataTepat(inputText, semuaKamus)
            if (hasil != null) {
                teksHasil = formatHasilTerjemahan(hasil)
                textViewHasil.text = teksHasil
                btnPlay.visibility = View.VISIBLE
                tts.speak(teksHasil, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                textViewHasil.text = "[kata tidak ditemukan]"
                btnPlay.visibility = View.GONE
            }

        }

        button2.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }

        val btnMic = findViewById<ImageView>(R.id.btnMic)

        btnMic.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID") // Gunakan bahasa Indonesia
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ucapkan kata dalam bahasa Banjar...")

            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Fitur voice input tidak tersedia di perangkat ini", Toast.LENGTH_SHORT).show()
            }
        }

        // Inisialisasi TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale("id", "ID") // Bahasa Indonesia
            }
        }

        btnPlay = findViewById(R.id.btnPlay)
        btnPlay.visibility = View.GONE
        btnPlay.setOnClickListener {
            teksHasil = textViewHasil.text.toString()
            tts.speak(teksHasil, TextToSpeech.QUEUE_FLUSH, null, null)
        }


        // tema gelap atau terang
        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeSwitch.isChecked = isNightMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val spokenText = result[0]
                val editText = findViewById<AutoCompleteTextView>(R.id.editText)
                editText.setText(spokenText)
                editText.setSelection(spokenText.length) // Pindahkan kursor ke akhir
                buttonbanjarindo.performClick()
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun formatHasilTerjemahan(entry: JSONObject): String {
        val word = entry.optString("dasar", "[?]").replaceFirstChar { it.uppercase() }
        val meanings = entry.optJSONArray("arti") ?: return "$word: [arti tidak ditemukan]"

        val hasil = StringBuilder()
        hasil.append("$word:\n")

        for (i in 0 until meanings.length()) {
            val meaning = meanings.optJSONObject(i)
            val definitions = meaning?.optJSONArray("definisi_umum") ?: continue

            for (j in 0 until definitions.length()) {
                val def = definitions.optJSONObject(j)
                val arti = def?.optString("definisi") ?: continue
                val jenis = def.optString("kelaskata", "")
                hasil.append("- ($jenis) $arti\n")

                val examples = def.optJSONArray("contoh")
                if (examples != null && examples.length() > 0) {
                    for (k in 0 until examples.length()) {
                        val ex = examples.optJSONObject(k)
                        val bjn = ex.optString("banjar", "")
                        val id = ex.optString("indonesia", "")
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
            val kataUtama = entry.optString("dasar", "").lowercase()
            if (kataUtama == input) return entry

            val derivatives = entry.optJSONArray("turunan") ?: continue
            for (i in 0 until derivatives.length()) {
                val turunan = derivatives.getJSONObject(i)
                val turunanWord = turunan.optString("dasar", "").lowercase()
                if (turunanWord == input) {
                    // Bungkus turunan menjadi struktur seperti entri utama
                    val wrapped = JSONObject()
                    wrapped.put("dasar", turunanWord)
                    wrapped.put("arti", JSONArray().apply {
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

