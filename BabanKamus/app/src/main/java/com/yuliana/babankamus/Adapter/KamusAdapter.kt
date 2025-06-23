package com.yuliana.babankamus.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.babankamus.Model.Kamus
import com.yuliana.babankamus.R
import java.util.*

class KamusAdapter(
    private var fullData: List<Kamus>
) : RecyclerView.Adapter<KamusAdapter.ViewHolder>() {

    private var filteredData: List<Kamus> = fullData

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teksKata: TextView = view.findViewById(R.id.textKata)
        val teksSuku: TextView = view.findViewById(R.id.textSukuKata)
        val teksDefinisi: TextView = view.findViewById(R.id.textDefinisi)
        val teksContoh: TextView = view.findViewById(R.id.textContoh)
        val teksTurunan: TextView = view.findViewById(R.id.textTurunan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_kamus, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = filteredData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredData[position]

        // Tampilkan kata dan suku kata
        holder.teksKata.text = item.kata
        holder.teksSuku.text = "Suku kata: ${item.sukukata}"

        // ====== DEFINISI ======
        val definisiText = item.definisi_umum.joinToString("\n") {
            "- ${it.definisi} (${it.kelaskata})"
        }
        holder.teksDefinisi.text = "Definisi:\n$definisiText"

        // ====== CONTOH (jika ada) ======
        val semuaContoh = item.definisi_umum
            .flatMap { it.contoh }
            .filter { it.banjar.isNotBlank() || it.indonesia.isNotBlank() }

        if (semuaContoh.isNotEmpty()) {
            val contohText = semuaContoh.joinToString("\n") {
                "- ${it.banjar} → ${it.indonesia}"
            }
            holder.teksContoh.visibility = View.VISIBLE
            holder.teksContoh.text = "Contoh:\n$contohText"
        } else {
            holder.teksContoh.visibility = View.GONE
        }

        // ====== TURUNAN (jika ada) ======
        if (item.turunan.isNotEmpty()) {
            val turunanText = item.turunan.joinToString("\n") {
                "• ${it.kata} (${it.sukukata})"
            }
            holder.teksTurunan.visibility = View.VISIBLE
            holder.teksTurunan.text = "Turunan:\n$turunanText"
        } else {
            holder.teksTurunan.visibility = View.GONE
        }
    }


    // Perbarui data
    fun updateData(newData: List<Kamus>) {
        fullData = newData
        filteredData = newData
        notifyDataSetChanged()
    }

    // Filter sederhana menggunakan in-text substring match
    fun filter(query: String) {
        if (query.isEmpty()) {
            filteredData = fullData
        } else {
            val lowerQuery = query.lowercase(Locale.getDefault())

            filteredData = fullData.filter { item ->
                item.kata.lowercase(Locale.getDefault()).contains(lowerQuery)
            }
        }
        notifyDataSetChanged()
    }
}

