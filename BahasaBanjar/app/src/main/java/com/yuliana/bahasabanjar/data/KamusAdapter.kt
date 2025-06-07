package com.yuliana.bahasabanjar.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.yuliana.bahasabanjar.databinding.ItemKataBinding
import com.yuliana.bahasabanjar.model.KamusEntry

class KamusAdapter()
//    private var listKamus: List<KamusEntry>,
//    private val onEditClick: (KamusEntry) -> Unit,
//    private val onDeleteClick: (KamusEntry) -> Unit
//) : RecyclerView.Adapter<KamusAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemKataBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(entry: KamusEntry) {
//            binding.textKata.text = entry.kata
//            binding.textSukuKata.text = entry.suku_kata
//
//            if (!entry.gambarUrl.isNullOrEmpty()) {
//                Glide.with(binding.imageKata.context)
//                    .load(entry.gambarUrl)
//                    .centerCrop()
//                    .into(binding.imageKata)
//            } else {
//                binding.imageKata.setImageResource(android.R.drawable.ic_menu_report_image)
//            }
//
//            // Tombol Edit
//            binding.btnEdit.setOnClickListener {
//                onEditClick(entry)
//            }
//
//            // Tombol Hapus
//            binding.btnHapus.setOnClickListener {
//                onDeleteClick(entry)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemKataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(listKamus[position])
//    }
//
//    override fun getItemCount(): Int = listKamus.size
//
//    fun updateData(newData: List<KamusEntry>) {
//        listKamus = newData
//        notifyDataSetChanged()
//    }
//}
