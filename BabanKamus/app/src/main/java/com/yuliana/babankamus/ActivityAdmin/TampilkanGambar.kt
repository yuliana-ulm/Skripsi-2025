package com.yuliana.babankamus.ActivityAdmin

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.babankamus.Adapter.KamusAdapter
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.Model.SuaraItem
import com.yuliana.babankamus.R

class TampilkanGambar : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KamusAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textLoading: TextView
    private val gambarList = ArrayList<GambarItem>()
    private val listSuara = mutableListOf<SuaraItem>()
    private val dataKamus = mutableListOf<Kamus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampilkan_gambar)

        recyclerView = findViewById(R.id.recyclerViewGambar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = KamusAdapter(dataKamus, listSuara, gambarList)
        recyclerView.adapter = adapter

        progressBar = findViewById(R.id.progressBar)
        textLoading = findViewById(R.id.textLoading)

        ambilSemuaGambar()
    }

    private fun ambilSemuaGambar() {
        progressBar.visibility = View.VISIBLE
        textLoading.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_gambar")
            .get()
            .addOnSuccessListener { result ->
                gambarList.clear()
                for (document in result) {
                    val gambarBase64 = document.getString("gambar_base64") ?: ""
                    val idDokumen = document.id

                    val gambarItem = GambarItem(
                        nama = idDokumen,
                        gambar_base64 = gambarBase64
                    )

                    gambarList.add(gambarItem)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data 😭", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
                textLoading.visibility = View.GONE
            }
    }
}
