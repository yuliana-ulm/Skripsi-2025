package com.yuliana.bahasabanjar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import com.google.gson.Gson
import com.yuliana.bahasabanjar.R.*

class KamusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KamusAdapter1
    private val dataKamus = mutableListOf<Kamus>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_kamus)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(id.recyclerKamus)
        adapter = KamusAdapter1(dataKamus)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //ini yang online
        ambilDataKamus()

        //ini yang offline
        loadData()

    }

    //yang ini tuh buat ambil data firestore dari online
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

    //ini yang bakalan ngambil dari offline gitu
    private fun loadData() {
        // Coba load data dari internal storage (offline)
        val jsonOffline = loadJsonFromInternalStorage(this, "kamusbanjar.json")
        if (jsonOffline != null) {
            val kamusList = parseJsonToKamusList(jsonOffline)
            setupAdapter(kamusList)
        } else {
            // Kalau kosong, load dari assets sebagai fallback
            val jsonAssets = loadJsonFromAssets(this, "kamusbanjar.json")
            if (jsonAssets != null) {
                val kamusList = parseJsonToKamusList(jsonAssets)
                setupAdapter(kamusList)
            }
        }

        // Lalu, coba fetch data terbaru dari Firestore (online)
        fetchDataFromFirestore(this,
            onComplete = { kamusList ->
                runOnUiThread {
                    setupAdapter(kamusList)
                }
            },
            onError = { e ->
                Log.e("KamusActivity", "Gagal ambil data dari Firestore", e)
            }
        )
    }

    private fun setupAdapter(data: List<Kamus>) {
        adapter = KamusAdapter1(data)
        recyclerView.adapter = adapter
    }

    fun loadJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun saveJsonToInternalStorage(context: Context, filename: String, data: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    fun loadJsonFromInternalStorage(context: Context, filename: String): String? {
        return try {
            context.openFileInput(filename).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun parseJsonToKamusList(jsonString: String): List<Kamus> {
        val gson = Gson()
        val kamusType = object : TypeToken<List<Kamus>>() {}.type
        return gson.fromJson(jsonString, kamusType)
    }

    fun fetchDataFromFirestore(context: Context, onComplete: (List<Kamus>) -> Unit, onError: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("kamus_banjar_indonesia")
            .get()
            .addOnSuccessListener { result ->
                val kamusList = mutableListOf<Kamus>()
                for (doc in result) {
                    // Parsing dokumen firestore ke model Kamus
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

                    val turunanRaw = doc.get("turunan") as? List<*>
                    val turunanList = mutableListOf<Turunan>()
                    turunanRaw?.forEach { item ->
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

                                    definisiTurunanList.add(Definisi(definisi, kelaskata, suara, contohList))
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

                    kamusList.add(
                        Kamus(
                            kata = kata,
                            sukukata = sukukata,
                            gambar = gambar,
                            definisi_umum = definisiUmumList,
                            turunan = turunanList
                        )
                    )
                }

                // Simpan data JSON ke internal storage
                val gson = Gson()
                val jsonString = gson.toJson(kamusList)
                saveJsonToInternalStorage(context, "kamusbanjar.json", jsonString)

                onComplete(kamusList)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}