package com.yuliana.bahasabanjar.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.bahasabanjar.databinding.ItemKamusBinding

class KamusAdapter(private var daftarKamus: List<KamusEntry>) :
RecyclerView.Adapter<KamusAdapter.KamusViewHolder>() {

    inner class KamusViewHolder(val binding: ItemKamusBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KamusViewHolder {
        val binding = ItemKamusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KamusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KamusViewHolder, position: Int) {
        val item = daftarKamus[position]
        with(holder.binding) {
            tvKata.text = item.kata
            tvSukuKata.text = "Suku Kata: ${item.sukukata}"
            tvAbjad.text = "Abjad: ${item.abjad}"

            // Definisi Umum
            val definisiText = item.definisi_umum.joinToString("\n") { def ->
                "- ${def.kelaskata}: ${def.definisi}"
            }
            tvDefinisi.text = if (definisiText.isNotBlank()) "Definisi:\n$definisiText" else "Definisi: -"

            // Turunan
            val turunanText = item.turunan.joinToString("\n") { tur ->
                "- ${tur.kata} (${tur.sukukata})"
            }
            tvTurunan.text = if (turunanText.isNotBlank()) "Turunan:\n$turunanText" else "Turunan: -"
        }
    }

    override fun getItemCount(): Int = daftarKamus.size

    fun updateData(newData: List<KamusEntry>) {
        daftarKamus = newData
        notifyDataSetChanged()
    }
}