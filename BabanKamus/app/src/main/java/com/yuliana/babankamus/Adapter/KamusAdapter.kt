package com.yuliana.babankamus.Adapter

//import keseluruahn dari kamus aktivity dulu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.R
import java.util.*
import kotlin.math.min

//import untuk adapter syuara
import android.media.MediaPlayer
import android.util.Base64
import android.widget.Button
import com.yuliana.babankamus.Model.SuaraItem
import java.io.File

//import untuk adapter gambyar eya :V semangat aku
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.app.Dialog
import com.yuliana.babankamus.Model.GambarItem

class KamusAdapter(
    private var fullData: List<Kamus>,
    private val daftar: List<SuaraItem>,
    private val gambarList: List<GambarItem>
) : RecyclerView.Adapter<KamusAdapter.ViewHolder>() {

    private var filteredData: List<Kamus> = fullData

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teksKata: TextView = view.findViewById(R.id.textKata)
        val teksSuku: TextView = view.findViewById(R.id.textSukuKata)
        val teksDefinisi: TextView = view.findViewById(R.id.textDefinisi)
        val teksTurunan: TextView = view.findViewById(R.id.textTurunan)
        val textNama: TextView = itemView.findViewById(R.id.textNamaSuara)
        val btnPutar: Button = itemView.findViewById(R.id.btnPutarSuara)
        val imageView: ImageView = itemView.findViewById(R.id.imageItemGambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_kamus, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = filteredData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //untuk kata dalam kamus
        val item = filteredData[position]

        holder.teksKata.text = item.kata
        holder.teksSuku.text = "Suku kata: ${item.sukukata}"

        val definisiText = item.definisi_umum.joinToString("\n") {
            "- ${it.definisi} (${it.kelaskata})"
        }

        holder.teksDefinisi.text = "Definisi:\n$definisiText"

        val turunanText = item.turunan.joinToString("\n") {
            "• ${it.kata} (${it.sukukata})"
        }

        holder.teksTurunan.text = "Turunan:\n$turunanText"

        //untuk suara
        val suaraItem = daftar.find { it.nama.equals(item.kata, ignoreCase = true) }

        if (suaraItem != null) {
            holder.textNama.text = suaraItem.nama
            holder.btnPutar.visibility = View.VISIBLE
            holder.btnPutar.setOnClickListener {
                val decoded = Base64.decode(suaraItem.suara_base64, Base64.DEFAULT)
                val tempFile = File.createTempFile("temp_suara", ".mp3")
                tempFile.writeBytes(decoded)

                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(tempFile.absolutePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        } else {
            holder.textNama.text = ""
            holder.btnPutar.visibility = View.GONE
        }

        //untuk gambar
        val gambarItem = gambarList.find { it.nama.equals(item.kata, ignoreCase = true) }

        if (gambarItem != null) {
            val decodedBytes = Base64.decode(gambarItem.gambar_base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imageView.setImageBitmap(bitmap)

            holder.imageView.setOnClickListener {
                val context = holder.itemView.context
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.dialog_image_popup)

                val imageView = dialog.findViewById<ImageView>(R.id.imagePopup)
                imageView.setImageDrawable(holder.imageView.drawable)

                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.show()
            }
        } else {
            // Gambar tidak ditemukan, bisa diset gambar placeholder atau disembunyikan
            holder.imageView.setImageResource(R.drawable.ic_launcher_background) // contoh placeholder
            holder.imageView.setOnClickListener(null)
        }
    }

    // Untuk memperbarui data (jika ada perubahan dari Firebase misalnya)
    fun updateData(newData: List<Kamus>) {
        fullData = newData
        filteredData = newData
        notifyDataSetChanged()
    }

    // Filter dengan Levenshtein
    fun filter(query: String) {
        if (query.isEmpty()) {
            filteredData = fullData
        } else {
            val lowerQuery = query.lowercase(Locale.getDefault())
            val maxDistance = 2

            filteredData = fullData.filter { item ->
                val kataLower = item.kata.lowercase(Locale.getDefault())
                val distance = levenshteinDistance(lowerQuery, kataLower)
                distance <= maxDistance || kataLower.contains(lowerQuery)
            }.sortedBy {
                levenshteinDistance(lowerQuery, it.kata.lowercase(Locale.getDefault()))
            }
        }
        notifyDataSetChanged()
    }

    // Algoritma Levenshtein Distance
    private fun levenshteinDistance(s: String, t: String): Int {
        val m = s.length
        val n = t.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s[i - 1] == t[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }
}