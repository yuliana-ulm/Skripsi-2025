package com.yuliana.bahasabanjar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.bahasabanjar.admin.Admin
import com.yuliana.bahasabanjar.user.Beranda
import com.yuliana.bahasabanjar.user.Beranda2

class Splashscreen : AppCompatActivity() {

    // Misalnya data login dari SharedPreferences, atau bisa juga variabel sementara
    private var username: String = "aku" // Ganti ini sesuai data login pengguna

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (username == "aku") {
                // Jika login sebagai admin
                val intent = Intent(this, Admin::class.java)
                startActivity(intent)
            } else if (username == "kamu") {
                // Jika login sebagai pengguna biasa
                val intent = Intent(this, Beranda::class.java)
                startActivity(intent)
            } else {
                // Jika login sebagai pengguna biasa
                val intent = Intent(this, Beranda2::class.java)
                startActivity(intent)
        }
            finish() // agar splash screen tidak bisa dikembalikan
        }, 3000) // 3000 ms = 3 detik
    }
}