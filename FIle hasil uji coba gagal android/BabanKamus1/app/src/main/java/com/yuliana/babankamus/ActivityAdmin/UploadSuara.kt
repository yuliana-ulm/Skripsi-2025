package com.yuliana.babankamus.ActivityAdmin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.R
import java.io.InputStream

class UploadSuara : AppCompatActivity() {

    private lateinit var soundView: EditText
    private lateinit var textFileTerpilih: TextView
    private lateinit var textUploading: TextView
    private lateinit var progressBarUpload: ProgressBar
    private lateinit var btnCancel: Button
    private lateinit var btnUploadSuara: Button
    private lateinit var btnPilihSuara: Button

    private var base64Suara: String = ""
    private var fileName: String = ""

    companion object {
        const val REQUEST_CODE_AUDIO = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_suara)

        btnCancel = findViewById(R.id.btnCancel)
        btnPilihSuara = findViewById(R.id.btnPilihSuara)
        soundView = findViewById(R.id.editTextNamaSuara)
        textFileTerpilih = findViewById(R.id.textFileTerpilih)
        textUploading = findViewById(R.id.textUploading)
        progressBarUpload = findViewById(R.id.progressBarUpload)

        soundView.setOnClickListener {
            pilihsuara()
        }

        btnCancel.setOnClickListener {
            soundView.text.clear()
            textFileTerpilih.text = "Belum ada file yang dipilih"
            base64Suara = ""

            // Kembali ke MainActivity
            val intent = Intent(this, DashAdmin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btnPilihSuara.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(Intent.createChooser(intent, "Pilih file suara"), REQUEST_CODE_AUDIO)
        }

        btnUploadSuara.setOnClickListener {
            val namaInput = soundView.text.toString().trim()
            if (namaInput.isEmpty()) {
                Toast.makeText(this, "Masukkan nama suara dulu yaa 😘", Toast.LENGTH_SHORT).show()
            } else if (base64Suara.isEmpty()) {
                Toast.makeText(this, "Pilih file suara dulu yaa sayang 🎶", Toast.LENGTH_SHORT).show()
            } else {
                uploadToFirestore(namaInput, base64Suara)
            }
        }
    }

    private fun pilihsuara() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "sound/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            val audioUri: Uri? = data.data
            audioUri?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                base64Suara = Base64.encodeToString(bytes, Base64.DEFAULT)
                fileName = it.lastPathSegment ?: "audio_file"
                textFileTerpilih.text = "File terpilih: $fileName"
            }
        }
    }

    private fun uploadToFirestore(namaInput: String, base64Suara: String) {
        textUploading.visibility = View.VISIBLE
        progressBarUpload.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()
        val suaraRef = db.collection("kamus_suara").document(namaInput) // Sesuai nama input

        val data = hashMapOf(
            "nama" to namaInput,
            "suara_base64" to base64Suara
        )

        suaraRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil diupload ke kamus_suara 🎉", Toast.LENGTH_SHORT).show()
                textUploading.visibility = View.GONE
                progressBarUpload.visibility = View.GONE

                // Reset isian
                soundView.setText("")
                textFileTerpilih.text = "Belum ada file yang dipilih"
                this@UploadSuara.base64Suara = ""
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal upload: ${e.message}", Toast.LENGTH_SHORT).show()
                textUploading.visibility = View.GONE
                progressBarUpload.visibility = View.GONE
            }
    }

}