package com.yuliana.bahasabanjar.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.data.Gambar
import com.yuliana.bahasabanjar.data.Kata
import com.yuliana.bahasabanjar.data.Pengguna
import com.yuliana.bahasabanjar.data.Suara
import com.yuliana.bahasabanjar.data.Turunan

class Admin : AppCompatActivity() {

    private lateinit var themeSwitch: SwitchCompat
    private lateinit var buttonAdmin: Button
    private lateinit var pindahkata: ImageButton
    private lateinit var pindahturunan: ImageButton
    private lateinit var pindahsuara: ImageButton
    private lateinit var pindahgambar: ImageButton
    private lateinit var pindahpengguna: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // tema gelap atau terang
        themeSwitch = findViewById(R.id.themeSwitch)

        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeSwitch.isChecked = isNightMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Informasi pengguna admin
        buttonAdmin = findViewById(R.id.buttonAdmin )

        buttonAdmin.setOnClickListener {
            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
        }

        // Berpindah halaman kata
        pindahkata = findViewById(R.id.Kata)
        pindahkata.setOnClickListener {
            val intent = Intent(this, Kata::class.java)
            startActivity(intent)
        }

        // Berpindah halaman turunan
        pindahturunan = findViewById(R.id.Turunan)
        pindahturunan.setOnClickListener {
            val intent = Intent(this, Turunan::class.java)
            startActivity(intent)
        }

        // Berpindah halaman suara
        pindahsuara = findViewById(R.id.Suara)
        pindahsuara.setOnClickListener {
            val intent = Intent(this, Suara::class.java)
            startActivity(intent)
        }

        // Berpindah halaman gambar
        pindahgambar = findViewById(R.id.Gambar)
        pindahgambar.setOnClickListener {
            val intent = Intent(this, Gambar::class.java)
            startActivity(intent)
        }

        // Berpindah halaman pengguna
        pindahpengguna = findViewById(R.id.Pengguna)
        pindahpengguna.setOnClickListener {
            val intent = Intent(this, Pengguna::class.java)
            startActivity(intent)
        }
    }
}