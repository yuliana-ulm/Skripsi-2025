package com.yuliana.babankamus.Adapter

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.R
import com.bumptech.glide.Glide

class GambarAdapter(
    private val data: List<GambarItem>,
    private val onEdit: (GambarItem) -> Unit,
    private val onDelete: (GambarItem) -> Unit
) : RecyclerView.Adapter<GambarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gambar: ImageView = view.findViewById(R.id.gambarView)
        val deskripsi: TextView = view.findViewById(R.id.deskripsiView)
        val editBtn: ImageButton = view.findViewById(R.id.btnEdit)
        val hapusBtn: ImageButton = view.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_gambar, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.deskripsi.text = item.deskripsi
        Glide.with(holder.itemView).load(item.url).into(holder.gambar)

        holder.editBtn.setOnClickListener { onEdit(item) }
        holder.hapusBtn.setOnClickListener { onDelete(item) }
    }
}