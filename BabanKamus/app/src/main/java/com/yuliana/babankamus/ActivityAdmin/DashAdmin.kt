package com.yuliana.babankamus.ActivityAdmin

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.yuliana.babankamus.KamusActivity
import com.yuliana.babankamus.R
import de.hdodenhof.circleimageview.CircleImageView

class DashAdmin : AppCompatActivity() {

    private lateinit var userAvatar: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userAvatar = findViewById(R.id.userAvatar)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: "?"

        val hurufAwal = email.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

        //Buat Bitmap dengan huruf
        val avatarBitmap = createAvatarBitmap(hurufAwal)
        userAvatar.setImageBitmap(avatarBitmap)

        //Button Lihat Kamus
        val btnLihatKamus: Button = findViewById(R.id.buttonLihatKamus)
        btnLihatKamus.setOnClickListener {
            startActivity(Intent(this, KamusActivity::class.java))
        }

        //Tambah Kata
        val btnTambah: ImageButton = findViewById(R.id.buttonTambah)
        btnTambah.setOnClickListener {
            startActivity(Intent(this, TambahKataActivity::class.java))
        }

        //Edit Kata
        val btnEdit: ImageButton = findViewById(R.id.buttonEdit)
        btnEdit.setOnClickListener {
            startActivity(Intent(this, EditKataActivity::class.java))
        }

        //Hapus Kata
        val btnHapus: ImageButton = findViewById(R.id.buttonHapus)
        btnHapus.setOnClickListener {
            startActivity(Intent(this, HapusKataActivity::class.java))
        }

        //Kelola Pengguna
        val btnKelola: ImageButton = findViewById(R.id.buttonKelolaPengguna)
        btnKelola.setOnClickListener {
            startActivity(Intent(this, KelolaPenggunaActivity::class.java))
        }

        // untuk logout
        val logoutButton = findViewById<ImageButton>(R.id.buttonLogout)
        userAvatar.setOnClickListener {
            logoutButton.visibility = if (logoutButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, KamusActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    //Fungsi untuk bikin bitmap huruf dalam lingkaran
    private fun createAvatarBitmap(initial: String): Bitmap {
        val size = 200
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 🟢 Warna latar belakang acak dari daftar hijau
        val greenShades = listOf("#4CAF50", "#81C784", "#66BB6A", "#388E3C", "#2E7D32")
        val paint = Paint().apply {
            color = Color.parseColor(greenShades.random())
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        // 🔵 Gambar lingkaran
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // 🔤 Tulis huruf
        paint.color = Color.WHITE
        paint.textSize = 96f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

        canvas.drawText(initial, (canvas.width / 2).toFloat(), yPos, paint)

        return bitmap
    }
}