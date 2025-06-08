package com.yuliana.bahasabanjar.mencoba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.model.KamusEntry

class AdminActivity : AppCompatActivity() {
    private lateinit var rvKamus: RecyclerView
    private val listKamus = mutableListOf<KamusEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityadmin)

        rvKamus = findViewById(R.id.rvKamus)
        val btnTambah = findViewById<Button>(R.id.btnTambah)

        rvKamus.layoutManager = LinearLayoutManager(this)

        btnTambah.setOnClickListener {
            // Form kosong untuk tambah
            startActivity(Intent(this, FormTambahActivity::class.java))
        }

        FirebaseUtil.db.collection("Kamus")
            .addSnapshotListener { value, _ ->
                listKamus.clear()
                value?.forEach {
                    val data = it.toObject(KamusEntry::class.java)
                    data.id = it.id
                    listKamus.add(data)
                }

                rvKamus.adapter = KamusAdapter(
                    listKamus,
                    onEditClick = { entry ->
                        // Kirim data via JSON ke Edit Activity
                        val gson = Gson()
                        val json = gson.toJson(entry)
                        val intent = Intent(this, EditKamusActivity::class.java)
                        intent.putExtra("data_kamus", json)
                        startActivity(intent)
                    },
                    onDeleteClick = { entry ->
                        FirebaseUtil.db.collection("Kamus").document(entry.id).delete()
                    }
                )
            }
    }
}
