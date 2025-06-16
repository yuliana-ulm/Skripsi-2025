package com.yuliana.babankamus.ActivityAdmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.babankamus.R

class DashAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tombol Upload Gambar
        val buttonUploadGambar = findViewById<Button>(R.id.buttonUploadGambar)
        buttonUploadGambar.setOnClickListener {
            val intent = Intent(this, UploadGambar::class.java)
            startActivity(intent)
        }

        // Tombol Upload Suara
        val buttonUploadSuara = findViewById<Button>(R.id.buttonUploadSuara)
        buttonUploadSuara.setOnClickListener {
            val intent = Intent(this, UploadSuara::class.java)
            startActivity(intent)
        }
    }
}