package com.yuliana.babankamus.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.Adapter.GambarAdapter
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.R
import java.io.File

class GambarActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1
    private val CAMERA_REQUEST = 2
    private val CAMERA_PERMISSION_CODE = 100

    private lateinit var imageUri: Uri
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GambarAdapter
    private val dataList = mutableListOf<GambarItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gambar)

        val deskripsiEdit = findViewById<EditText>(R.id.deskripsiInput)
        val pilihButton = findViewById<Button>(R.id.pilihFoto)
        val kameraButton = findViewById<Button>(R.id.kameraFoto)
        val uploadButton = findViewById<Button>(R.id.uploadBtn)

        recyclerView = findViewById(R.id.recyclerGambar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GambarAdapter(dataList,
            onEdit = { item -> editData(item) },
            onDelete = { item -> deleteData(item) }
        )
        recyclerView.adapter = adapter

        pilihButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        kameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                bukaKamera()
            }
        }

        uploadButton.setOnClickListener {
            val deskripsi = deskripsiEdit.text.toString().trim()
            if (::imageUri.isInitialized && deskripsi.isNotEmpty()) {
                uploadImageToFirebase(imageUri, deskripsi)
            } else {
                Toast.makeText(this, "Lengkapi gambar dan deskripsi", Toast.LENGTH_SHORT).show()
            }
        }

        ambilData()
    }

    private fun bukaKamera() {
        val file = File.createTempFile("IMG_", ".jpg", externalCacheDir)
        imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bukaKamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> imageUri = data?.data!!
                CAMERA_REQUEST -> {
                    // imageUri sudah di-set sebelumnya di bukaKamera()
                    Toast.makeText(this, "Foto dari kamera berhasil diambil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri, deskripsi: String) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val storageRef = FirebaseStorage.getInstance().getReference("gambar/$filename")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    val data = mapOf("url" to url.toString(), "deskripsi" to deskripsi)
                    FirebaseFirestore.getInstance().collection("gambar_kamus").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Berhasil upload", Toast.LENGTH_SHORT).show()
                            ambilData()
                        }
                }
            }
    }

    private fun ambilData() {
        FirebaseFirestore.getInstance().collection("gambar_kamus")
            .get()
            .addOnSuccessListener { result ->
                dataList.clear()
                for (doc in result) {
                    val url = doc.getString("url") ?: ""
                    val desc = doc.getString("deskripsi") ?: ""
                    dataList.add(GambarItem(doc.id, url, desc))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun deleteData(item: GambarItem) {
        FirebaseFirestore.getInstance().collection("gambar_kamus").document(item.id).delete()
        FirebaseStorage.getInstance().getReferenceFromUrl(item.url).delete()
        Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
        ambilData()
    }

    private fun editData(item: GambarItem) {
        val input = EditText(this)
        input.setText(item.deskripsi)

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Edit Deskripsi")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val update = mapOf("deskripsi" to input.text.toString())
                FirebaseFirestore.getInstance().collection("gambar_kamus").document(item.id).update(update)
                ambilData()
            }
            .setNegativeButton("Batal", null)
            .create()
        dialog.show()
    }
}
