package com.yuliana.babankamus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.Button
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.yuliana.babankamus.ActivityAdmin.DashAdmin
import com.yuliana.babankamus.Adapter.KamusAdapter
import com.yuliana.babankamus.Model.Contoh
import com.yuliana.babankamus.Model.Definisi
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.Model.Turunan
import java.io.IOException

class KamusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KamusAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textLoading: TextView
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var loginadmin: Button
    private lateinit var editTextSearch: AutoCompleteTextView
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
        themeSwitch = findViewById(R.id.themeSwitch)
        editTextSearch = findViewById(R.id.editTextSearch)
        loginadmin = findViewById<Button>(R.id.buttonAdmin)
        adapter = KamusAdapter(dataKamus)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        progressBar = findViewById(R.id.progressBar)
        textLoading = findViewById(R.id.textLoading)

        ambilDataKamusDariFirestore()

        loginadmin.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Sudah login → masuk ke halaman admin
                val intent = Intent(this, DashAdmin::class.java)
                startActivity(intent)
            } else {
                // Belum login → redirect ke halaman Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Silakan login dulu untuk mengakses Admin", Toast.LENGTH_SHORT).show()
            }
        }


        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                adapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

    private fun ambilDataKamusDariFirestore() {
        FirebaseFirestore.getInstance().collection("kamus_banjar_indonesia")
            .get()
            .addOnSuccessListener { hasil ->
                dataKamus.clear()
                for (doc in hasil) {
                    val kata = doc.getString("kata") ?: ""
                    val sukukata = doc.getString("sukukata") ?: ""

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
                                        contoh = contohList
                                    )
                                } else null
                            } ?: emptyList()

                            Turunan(
                                kata = t["kata"] as? String ?: "",
                                sukukata = t["sukukata"] as? String ?: "",
                                definisi_umum = definisiTurunan
                            )
                        } else null
                    } ?: emptyList()

                    dataKamus.add(Kamus(kata, sukukata, definisiList, turunanList))
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data", Toast.LENGTH_SHORT).show()
                loadData()
            }
    }

    private fun loadData() {
        val jsonOffline = loadJsonFromInternalStorage(this, "kamusbanjar.json")
        if (jsonOffline != null) {
            val kamusList = parseJsonToKamusList(jsonOffline)
            setupAdapter(kamusList)
        } else {
            val jsonAssets = loadJsonFromAssets(this, "kamusbanjar.json")
            if (jsonAssets != null) {
                val kamusList = parseJsonToKamusList(jsonAssets)
                setupAdapter(kamusList)
            }
        }
    }

    private fun setupAdapter(data: List<Kamus>) {
        adapter = KamusAdapter(data)
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
}
