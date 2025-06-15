package com.yuliana.babankamus.ActivityAdmin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.R
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UploadGambar : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editTextNama: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnCancel: Button
    private lateinit var textUploading: TextView
    private lateinit var progressBarUpload: ProgressBar
    private var base64Image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_gambar)

        imageView = findViewById(R.id.imageView)
        editTextNama = findViewById(R.id.editTextNama)
        btnUpload = findViewById(R.id.btnUpload)
        btnCancel = findViewById(R.id.btnCancel)
        textUploading = findViewById(R.id.textUploading)
        progressBarUpload = findViewById(R.id.progressBarUpload)

        imageView.setOnClickListener {
            pilihGambarDariGaleri()
        }

        val btnKeUploadSuara = findViewById<Button>(R.id.btnKeUploadSuara)
        btnKeUploadSuara.setOnClickListener {
            val intent = Intent(this, UploadSuara::class.java)
            startActivity(intent)
        }

        btnUpload.setOnClickListener {
            val namaInput = editTextNama.text.toString().trim()

            if (namaInput.isEmpty()) {
                Toast.makeText(this, "Masukkan nama gambar dulu yaa 😘", Toast.LENGTH_SHORT).show()
            } else if (base64Image.isEmpty()) {
                Toast.makeText(this, "Pilih gambar dulu yaa sayang 💕", Toast.LENGTH_SHORT).show()
            } else {
                uploadToFirestore(namaInput, base64Image)
            }
        }

        btnCancel.setOnClickListener {
            // Kosongkan isian
            editTextNama.setText("")
            imageView.setImageResource(R.drawable.ic_launcher_background)
            base64Image = ""

            // Kembali ke MainActivity
            val intent = Intent(this, TampilkanGambar::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun pilihGambarDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            base64Image = encodeImageToBase64(bitmap)
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun uploadToFirestore(namaInput: String, base64Image: String) {
        textUploading.visibility = View.VISIBLE
        progressBarUpload.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()
        val data = hashMapOf("gambar_base64" to base64Image)

        db.collection("kamus_gambar")
            .document(namaInput)
            .set(data)
            .addOnSuccessListener {
                textUploading.visibility = View.GONE
                progressBarUpload.visibility = View.GONE

                Toast.makeText(this, "Berhasil disimpan 🥰", Toast.LENGTH_SHORT).show()
                editTextNama.setText("")
                imageView.setImageResource(R.drawable.ic_launcher_background)
                this@UploadGambar.base64Image = ""
            }
            .addOnFailureListener {
                textUploading.visibility = View.GONE
                progressBarUpload.visibility = View.GONE

                Toast.makeText(this, "Gagal menyimpan 😢", Toast.LENGTH_SHORT).show()
            }
    }
}
