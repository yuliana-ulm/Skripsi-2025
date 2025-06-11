package com.yuliana.babankamus.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuliana.babankamus.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.Adapter.GambarAdapter
import com.yuliana.babankamus.Model.GambarItem

class TampilkanGambar : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GambarAdapter
    private val gambarList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tampilkan_gambar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val stringList: MutableList<String> = listOf("url1", "url2", "url3").toMutableList()

        val gambarList: List<GambarItem> = stringList.map { url ->
            GambarItem(url = url)
        }

        recyclerView = findViewById(R.id.recyclerViewGambar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GambarAdapter(gambarList)
        recyclerView.adapter = adapter

        ambilGambarDariFirestore()
    }

    private fun ambilGambarDariFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_gambar")
            .orderBy("timestamp") 
            .get()
            .addOnSuccessListener { result ->
                gambarList.clear()
                for (document in result) {
                    val base64 = document.getString("gambar_base64")
                    if (base64 != null) {
                        gambarList.add(base64)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // isi dengan gagal ambil data
            }
    }
}