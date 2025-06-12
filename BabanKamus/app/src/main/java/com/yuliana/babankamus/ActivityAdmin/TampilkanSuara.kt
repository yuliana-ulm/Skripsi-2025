package com.yuliana.babankamus.ActivityAdmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.Adapter.SuaraAdapter
import com.yuliana.babankamus.Model.SuaraItem
import com.yuliana.babankamus.R

class TampilkanSuara : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val listSuara = mutableListOf<SuaraItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampilkan_suara)

        recyclerView = findViewById(R.id.recyclerViewSuara)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = SuaraAdapter(listSuara)
        recyclerView.adapter = adapter

        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_suara")
            .get()
            .addOnSuccessListener { result ->
                listSuara.clear()
                for (doc in result) {
                    val nama = doc.getString("nama") ?: ""
                    val suara = doc.getString("suara_base64") ?: ""
                    listSuara.add(SuaraItem(nama, suara))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data suara", Toast.LENGTH_SHORT).show()
            }
    }
}
