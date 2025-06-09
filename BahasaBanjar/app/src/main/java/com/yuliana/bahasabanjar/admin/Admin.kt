package com.yuliana.bahasabanjar

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yuliana.bahasabanjar.model.KamusAdapter
import com.yuliana.bahasabanjar.databinding.ActivityAdminBinding
import com.yuliana.bahasabanjar.model.Contoh
import com.yuliana.bahasabanjar.model.Definisi
import com.yuliana.bahasabanjar.model.KamusEntry
import com.yuliana.bahasabanjar.model.Turunan

class Admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var adapter: KamusAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = KamusAdapter(emptyList())
        binding.rvKamus.layoutManager = LinearLayoutManager(this)
        binding.rvKamus.adapter = adapter

        ambilDataKamus()
    }

    private fun ambilDataKamus() {
        FirebaseFirestore.getInstance()
            .collection("kamus_banjar_indonesia")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val kata = doc.getString("kata") ?: ""
                    val sukukata = doc.getString("sukukata") ?: ""
                    val gambar = doc.getString("gambar") ?: ""

                    val definisiUmumRaw = doc.get("definisi_umum") as? List<*>
                    val definisiUmumList = mutableListOf<Definisi>()
                    definisiUmumRaw?.forEach { def ->
                        if (def is Map<*, *>) {
                            val definisi = def["definisi"] as? String ?: ""
                            val kelaskata = def["kelaskata"] as? String ?: ""
                            val suara = def["suara"] as? String ?: ""

                            val contohRaw = def["contoh"] as? List<*>
                            val contohList = contohRaw?.mapNotNull { con ->
                                if (con is Map<*, *>) {
                                    Contoh(
                                        banjar = con["banjar"] as? String ?: "",
                                        indonesia = con["indonesia"] as? String ?: ""
                                    )
                                } else null
                            } ?: emptyList()

                            definisiUmumList.add(Definisi(definisi, kelaskata, suara, contohList))
                        }
                    }

                    val turunanList = mutableListOf<Turunan>()
                    val turunanRaw = doc.get("turunan")
                    if (turunanRaw is List<*>) {
                        for (item in turunanRaw) {
                            if (item is Map<*, *>) {
                                val kataTurunan = item["kata"] as? String ?: ""
                                val sukukataTurunan = item["sukukata"] as? String ?: ""
                                val gambarTurunan = item["gambar"] as? String ?: ""

                                val definisiTurunanRaw = item["definisi_umum"] as? List<*>
                                val definisiTurunanList = mutableListOf<Definisi>()
                                definisiTurunanRaw?.forEach { def ->
                                    if (def is Map<*, *>) {
                                        val definisi = def["definisi"] as? String ?: ""
                                        val kelaskata = def["kelaskata"] as? String ?: ""
                                        val suara = def["suara"] as? String ?: ""

                                        val contohRaw = def["contoh"] as? List<*>
                                        val contohList = contohRaw?.mapNotNull { con ->
                                            if (con is Map<*, *>) {
                                                Contoh(
                                                    banjar = con["banjar"] as? String ?: "",
                                                    indonesia = con["indonesia"] as? String ?: ""
                                                )
                                            } else null
                                        } ?: emptyList()

                                        definisiTurunanList.add(
                                            Definisi(
                                                definisi,
                                                kelaskata,
                                                suara,
                                                contohList
                                            )
                                        )
                                    }
                                }

                                turunanList.add(
                                    Turunan(
                                        kata = kataTurunan,
                                        sukukata = sukukataTurunan,
                                        gambar = gambarTurunan,
                                        definisi_umum = definisiTurunanList
                                    )
                                )
                            }
                        }
                    }

                    // Logging per dokumen
                    Log.d("FirebaseKamus", "Kata: $kata")
                    Log.d("FirebaseKamus", "Definisi umum: ${definisiUmumList.size} item")
                    Log.d("FirebaseKamus", "Jumlah turunan: ${turunanList.size} kata")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseKamus", "Gagal ambil data", e)
            }
    }
}



//package com.yuliana.bahasabanjar.admin
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.appcompat.widget.SwitchCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.yuliana.bahasabanjar.R
//import com.yuliana.bahasabanjar.admin.Gambar
//
//
//class Admin : AppCompatActivity() {
//
//    private lateinit var themeSwitch: SwitchCompat
//    private lateinit var buttonAdmin: Button
//    private lateinit var pindahkata: ImageButton
//    private lateinit var pindahturunan: ImageButton
//    private lateinit var pindahsuara: ImageButton
//    private lateinit var pindahgambar: ImageButton
//    private lateinit var pindahpengguna: ImageButton
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_admin)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // tema gelap atau terang
//        themeSwitch = findViewById(R.id.themeSwitch)
//
//        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
//        themeSwitch.isChecked = isNightMode
//
//        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }
//
//        // Informasi pengguna admin
//        buttonAdmin = findViewById(R.id.buttonAdmin )
//
//        buttonAdmin.setOnClickListener {
//            Toast.makeText(this, "Button 2 clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        // Berpindah halaman kata
//        pindahkata = findViewById(R.id.Kata)
//        pindahkata.setOnClickListener {
//            val intent = Intent(this, Kata::class.java)
//            startActivity(intent)
//        }
//
//        // Berpindah halaman turunan
//        pindahturunan = findViewById(R.id.Turunan)
//        pindahturunan.setOnClickListener {
//            val intent = Intent(this, Turunan::class.java)
//            startActivity(intent)
//        }
//
//        // Berpindah halaman suara
//        pindahsuara = findViewById(R.id.Suara)
//        pindahsuara.setOnClickListener {
//            val intent = Intent(this, Suara::class.java)
//            startActivity(intent)
//        }
//
//        // Berpindah halaman gambar
//        pindahgambar = findViewById(R.id.Gambar)
//        pindahgambar.setOnClickListener {
//            val intent = Intent(this, Gambar::class.java)
//            startActivity(intent)
//        }
//
//        // Berpindah halaman pengguna
//        pindahpengguna = findViewById(R.id.Pengguna)
//        pindahpengguna.setOnClickListener {
//            val intent = Intent(this, Pengguna::class.java)
//            startActivity(intent)
//        }
//    }
//}
