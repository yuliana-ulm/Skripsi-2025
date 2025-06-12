package com.yuliana.babankamus.Adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yuliana.babankamus.Model.GambarItem
import com.yuliana.babankamus.R

class GambarAdapter(private val gambarList: List<GambarItem>) :
    RecyclerView.Adapter<GambarAdapter.GambarViewHolder>() {

    class GambarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageItemGambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GambarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gambar, parent, false)
        return GambarViewHolder(view)
    }

    override fun onBindViewHolder(holder: GambarViewHolder, position: Int) {
        val item = gambarList[position]
        val decodedBytes = Base64.decode(item.gambar_base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.imageView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int = gambarList.size
}
