package com.yuliana.bahasabanjar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.bahasabanjar.user.Beranda

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tunda selama 3 detik sebelum masuk ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
            finish() // agar splash screen tidak bisa dikembalikan
        }, 3000) // 3000 ms = 3 detik
    }
}