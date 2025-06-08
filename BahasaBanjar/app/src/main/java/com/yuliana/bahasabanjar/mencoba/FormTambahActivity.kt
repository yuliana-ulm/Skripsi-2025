package com.yuliana.bahasabanjar.mencoba

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.model.KamusEntry

class FormTambahActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_tambah)

        val etKata = findViewById<EditText>(R.id.etKata)
        val etArti = findViewById<EditText>(R.id.etArti)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val data = intent.getSerializableExtra("data") as? KamusEntry

        if (data != null) {
            etKata.setText(data.kata)
            etArti.setText(data.arti)
        }

        btnSimpan.setOnClickListener {
            val entry = KamusEntry(
                kata = etKata.text.toString(),
                arti = etArti.text.toString()
            )
            if (data != null) {
                FirebaseUtil.db.collection("Kamus").document(data.id).set(entry)
            } else {
                FirebaseUtil.db.collection("Kamus").add(entry)
            }
            finish()
        }
    }
}
