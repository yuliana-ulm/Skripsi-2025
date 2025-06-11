package com.yuliana.babankamus.Adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.R

class GambarAdapter(private val dataList: List<GambarItem>) :
    RecyclerView.Adapter<GambarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val deskripsiView: TextView = view.findViewById(R.id.deskripsiView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gambar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.deskripsiView.text = item.deskripsi

        // Ubah url menjadi gambar
        Glide.with(holder.itemView.context)
            .load(item.url)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = dataList.size
}
