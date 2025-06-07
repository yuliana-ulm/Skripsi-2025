package com.yuliana.bahasabanjar.data

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.admin.tambah_kata
import com.yuliana.bahasabanjar.databinding.ActivityKataBinding
import com.yuliana.bahasabanjar.model.KamusEntry
//import com.yuliana.bahasabanjar.FirestoreUtil
import com.yuliana.bahasabanjar.admin.edit_kata

class Kata : AppCompatActivity() {

    private lateinit var binding: ActivityKataBinding
    private lateinit var kamusAdapter: KamusAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val daftarKamus = mutableListOf<KamusEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kata)

//        binding = ActivityKataBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
//        // Setup RecyclerView
//        kamusAdapter = KamusAdapter(daftarKamus)
//        binding.recyclerKamus.layoutManager = LinearLayoutManager(this)
//        binding.recyclerKamus.adapter = kamusAdapter

//        // Ambil data dari Firestore
//        ambilDataKamus()
//
//        // Event tombol tambah
//        binding.btnTambah.setOnClickListener {
//            val intent = Intent(this, tambah_kata::class.java)
//            startActivity(intent)
//        }
//
//        // Event tombol kembali
//        binding.btnKembali.setOnClickListener {
//            finish()
//        }
//
//        // Fitur pencarian
//        binding.inputCari.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                filterKamus(s.toString())
//            }
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })
//
//        kamusAdapter = KamusAdapter(daftarKamus,
//            onEditClick = { entry ->
//                // Arahkan ke activity edit, atau tampilkan dialog
//                val intent = Intent(this, edit_kata::class.java)
//                intent.putExtra("data_kata", entry) // Pastikan KamusEntry Serializable/Parcelable
//                startActivity(intent)
//            },
//            onDeleteClick = { entry ->
//                FirebaseFirestore.getInstance()
//                    .collection("Kamus")
//                    .whereEqualTo("kata", entry.kata)
//                    .get()
//                    .addOnSuccessListener { documents ->
//                        for (doc in documents) {
//                            doc.reference.delete()
//                        }
//                        ambilDataKamus() // refresh data
//                    }
//            }
//        )
//    }

//    private fun ambilDataKamus() {
//        firestore.collection("Kamus")
//            .get()
//            .addOnSuccessListener { result ->
//                daftarKamus.clear()
//                for (document in result) {
//                    val entry = document.toObject(KamusEntry::class.java)
//                    daftarKamus.add(entry)
//                }
//                kamusAdapter.notifyDataSetChanged()
//            }
//    }
//
//    private fun filterKamus(keyword: String) {
//        val filtered = daftarKamus.filter {
//            it.kata.contains(keyword, ignoreCase = true)
//        }
//        kamusAdapter.updateData(filtered)
//    }
//}