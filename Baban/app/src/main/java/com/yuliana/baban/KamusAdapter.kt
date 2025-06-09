package com.yuliana.baban

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KamusAdapter(
    private val data: List<Kamus>
) : RecyclerView.Adapter<KamusAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teksKata: TextView = view.findViewById(R.id.textKata)
        val teksSuku: TextView = view.findViewById(R.id.textSukuKata)
        val teksDefinisi: TextView = view.findViewById(R.id.textDefinisi)
        val teksTurunan: TextView = view.findViewById(R.id.textTurunan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_kamus, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
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
    }
}
