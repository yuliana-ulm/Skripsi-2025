//package com.yuliana.babankamus.Adapter
//
//import android.media.MediaPlayer
//import android.util.Base64
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.yuliana.babankamus.Model.SuaraItem
//import com.yuliana.babankamus.R
//import java.io.File
//
//class SuaraAdapter(private val daftar: List<SuaraItem>) :
//    RecyclerView.Adapter<SuaraAdapter.SuaraViewHolder>() {
//
//    inner class SuaraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val textNama: TextView = itemView.findViewById(R.id.textNamaSuara)
//        val btnPutar: Button = itemView.findViewById(R.id.btnPutarSuara)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuaraViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_suara, parent, false)
//        return SuaraViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: SuaraViewHolder, position: Int) {
//        val data = daftar[position]
//        holder.textNama.text = data.nama
//
//        holder.btnPutar.setOnClickListener {
//            val decoded = Base64.decode(data.suara_base64, Base64.DEFAULT)
//            val tempFile = File.createTempFile("temp_suara", ".mp3")
//            tempFile.writeBytes(decoded)
//
//            val mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(tempFile.absolutePath)
//            mediaPlayer.prepare()
//            mediaPlayer.start()
//        }
//    }
//
//    override fun getItemCount(): Int = daftar.size
//}
