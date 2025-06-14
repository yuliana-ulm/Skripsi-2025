package com.yuliana.babankamus.ActivityAdmin

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import com.google.gson.Gson
import com.yuliana.babankamus.Adapter.KamusAdapter
import com.yuliana.babankamus.Model.Contoh
import com.yuliana.babankamus.Model.Definisi
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.Model.SuaraItem
import com.yuliana.babankamus.Model.Turunan
import com.yuliana.babankamus.R

class KamusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KamusAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textLoading: TextView
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var editTextSearch: AutoCompleteTextView
    private lateinit var toggleButton: Button
    private val listSuara = mutableListOf<SuaraItem>()
    private val gambarList = ArrayList<GambarItem>()
    private val suaraMap = mutableMapOf<String, String>()
    private val gambarMap = mutableMapOf<String, String>()
    private val dataKamus = mutableListOf<Kamus>()
    private var isBanjarToIndo = true


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
        themeSwitch = findViewById(R.id.themeSwitch)
        editTextSearch = findViewById(R.id.editTextSearch)
        toggleButton = findViewById(R.id.buttonToggleBahasa)
        adapter = KamusAdapter(dataKamus, listSuara, gambarList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        progressBar = findViewById(R.id.progressBar)
        textLoading = findViewById(R.id.textLoading)

        //mengubah bahasa
        val sharedPrefs = getSharedPreferences("KamusPrefs", MODE_PRIVATE)
        isBanjarToIndo = sharedPrefs.getBoolean("isBanjarToIndo", true)
        toggleButton.text = if (isBanjarToIndo) "Banjar → Indonesia" else "Indonesia → Banjar"

        toggleButton.setOnClickListener {
            isBanjarToIndo = !isBanjarToIndo
            toggleButton.text = if (isBanjarToIndo) "Banjar → Indonesia" else "Indonesia → Banjar"

            // Simpan ke SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putBoolean("isBanjarToIndo", isBanjarToIndo)
            editor.apply()

            //data kamus berubah
            ambilDataKamus()
        }

        // Tombol Upload Gambar
        val buttonUploadGambar = findViewById<Button>(R.id.buttonUploadGambar)
        buttonUploadGambar.setOnClickListener {
            val intent = Intent(this, UploadGambar::class.java)
            startActivity(intent)
        }

        // Tombol Upload Suara
        val buttonUploadSuara = findViewById<Button>(R.id.buttonUploadSuara)
        buttonUploadSuara.setOnClickListener {
            val intent = Intent(this, UploadSuara::class.java)
            startActivity(intent)
        }

        //untuk pencarian
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                adapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // tema gelap atau terang
        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeSwitch.isChecked = isNightMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    //yang ini tuh buat ambil data firestore dari online khusus suara aja sih :V
    private fun ambilDataKamus() {
        //menyesuaikan pindah bahasa
        val koleksi = if (isBanjarToIndo) "kamus_banjar_indonesia" else "kamus_indonesia_banjar"

        // Ambil dulu data suara
        FirebaseFirestore.getInstance().collection("kamus_suara")
            .get()
            .addOnSuccessListener { result ->
                listSuara.clear()
                suaraMap.clear()
                for (doc in result) {
                    val nama = doc.getString("nama") ?: ""
                    val suara = doc.getString("suara_base64") ?: ""
                    suaraMap[nama] = suara
                    listSuara.add(SuaraItem(nama, suara))
                }

                recyclerView.adapter = adapter

                // Setelah data suara siap, ambil data kamus
                ambilDataKamusDariFirestore(koleksi)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data suara", Toast.LENGTH_SHORT).show()
            }

        //ambil juga data gambar dulu
        progressBar.visibility = View.VISIBLE
        textLoading.visibility = View.VISIBLE

        FirebaseFirestore.getInstance().collection("kamus_gambar")
            .get()
            .addOnSuccessListener { result ->
                gambarList.clear()
                suaraMap.clear()
                for (document in result) {
                    val gambarBase64 = document.getString("gambar_base64") ?: ""
                    val idDokumen = document.id

                    val gambarItem = GambarItem(
                        nama = idDokumen,
                        gambar_base64 = gambarBase64
                    )
                    gambarMap[idDokumen] = gambarBase64
                    gambarList.add(gambarItem)
                }

                recyclerView.adapter = adapter

                // Setelah data gambarnya siap, ambil data kamus
                ambilDataKamusDariFirestore(koleksi)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data 😭", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
                textLoading.visibility = View.GONE
            }

    }
    //yang ini tuh buat ambil data firestore dari online
    private fun ambilDataKamusDariFirestore(koleksi: String) {
        FirebaseFirestore.getInstance().collection(koleksi)
            .get()
            .addOnSuccessListener { hasil ->
                dataKamus.clear()
                for (doc in hasil) {
                    val kata = doc.getString("kata") ?: ""
                    val sukukata = doc.getString("sukukata") ?: ""
                    val gambar = gambarMap[kata] ?: "" //ambil gambar yg sesuai

                    val suaraBase64 = suaraMap[kata] ?: "" // ambil suara yang cocok

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
                                suara = suaraBase64, // pakai suara dari map
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
                                        suara = "",
                                        contoh = contohList
                                    )
                                } else null
                            } ?: emptyList()

                            Turunan(
                                kata = t["kata"] as? String ?: "",
                                sukukata = t["sukukata"] as? String ?: "",
                                gambar = "",
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
                loadData()
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
        fetchDataFromFirestore(
            context = this,
            isBanjarToIndo = isBanjarToIndo,
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
        adapter = KamusAdapter(data, listSuara, gambarList)
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

    fun fetchDataFromFirestore(
        context: Context,
        isBanjarToIndo: Boolean,
        onComplete: (List<Kamus>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val koleksinya = if (isBanjarToIndo) "kamus_banjar_indonesia" else "kamus_indonesia_banjar"

        FirebaseFirestore.getInstance().collection(koleksinya)
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