package com.yuliana.bahasabanjar.mencoba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.model.KamusEntry

class KamusAdapter(
    private val list: List<KamusEntry>,
    private val onEditClick: (KamusEntry) -> Unit,
    private val onDeleteClick: (KamusEntry) -> Unit
) : RecyclerView.Adapter<KamusAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKata = itemView.findViewById<TextView>(R.id.tvKata)
        val btnEdit = itemView.findViewById<Button>(R.id.btnEdit)
        val btnHapus = itemView.findViewById<Button>(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kamus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvKata.text = data.kata
        holder.btnEdit.setOnClickListener { onEditClick(data) }
        holder.btnHapus.setOnClickListener { onDeleteClick(data) }
    }

    override fun getItemCount(): Int = list.size
}