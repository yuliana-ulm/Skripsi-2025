package com.yuliana.baban

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class KamusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KamusAdapter
    private val dataKamus = mutableListOf<Kamus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kamus)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerKamus)
        adapter = KamusAdapter(dataKamus)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        ambilDataKamus()
    }

    private fun ambilDataKamus() {
        FirebaseFirestore.getInstance().collection("kamus_banjar_indonesia")
            .get()
            .addOnSuccessListener { hasil ->
                dataKamus.clear()
                for (doc in hasil) {
                    val kata = doc.getString("kata") ?: ""
                    val sukukata = doc.getString("sukukata") ?: ""
                    val gambar = doc.getString("gambar") ?: ""

                    val definisiList = (doc["definisi_umum"] as? List<*>)?.mapNotNull { item ->
                        if (item is Map<*, *>) {
                            val contohList = (item["contoh"] as? List<*>)?.mapNotNull { c ->
                                if (c is Map<*, *>) Contoh(
                                    banjar = c["banjar"] as? String ?: "",
                                    indonesia = c["indonesia"] as? String ?: ""
                                ) else null
                            } ?: emptyList()

                            Definisi(
                                definisi = item["definisi"] as? String ?: "",
                                kelaskata = item["kelaskata"] as? String ?: "",
                                suara = item["suara"] as? String ?: "",
                                contoh = contohList
                            )
                        } else null
                    } ?: emptyList()

                    val turunanList = (doc["turunan"] as? List<*>)?.mapNotNull { t ->
                        if (t is Map<*, *>) {
                            val definisiTurunan = (t["definisi_umum"] as? List<*>)?.mapNotNull { d ->
                                if (d is Map<*, *>) {
                                    val contohList = (d["contoh"] as? List<*>)?.mapNotNull { c ->
                                        if (c is Map<*, *>) Contoh(
                                            banjar = c["banjar"] as? String ?: "",
                                            indonesia = c["indonesia"] as? String ?: ""
                                        ) else null
                                    } ?: emptyList()

                                    Definisi(
                                        definisi = d["definisi"] as? String ?: "",
                                        kelaskata = d["kelaskata"] as? String ?: "",
                                        suara = d["suara"] as? String ?: "",
                                        contoh = contohList
                                    )
                                } else null
                            } ?: emptyList()

                            Turunan(
                                kata = t["kata"] as? String ?: "",
                                sukukata = t["sukukata"] as? String ?: "",
                                gambar = t["gambar"] as? String ?: "",
                                definisi_umum = definisiTurunan
                            )
                        } else null
                    } ?: emptyList()

                    dataKamus.add(Kamus(kata, sukukata, gambar, definisiList, turunanList))
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
    }
}