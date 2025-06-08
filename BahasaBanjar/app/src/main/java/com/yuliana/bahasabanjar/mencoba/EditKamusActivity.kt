package com.yuliana.bahasabanjar.mencoba

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.yuliana.bahasabanjar.model.KamusEntry

class EditKamusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Contoh pemanggilan data dari Intent
        val json = intent.getStringExtra("data_kamus")
        val data = Gson().fromJson(json, KamusEntry::class.java)

        // Sekarang kamu bisa pakai data.kata, data.definisi_umum, dll
    }
}
