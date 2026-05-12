package com.example.demo_list_v2

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class SongAdapter(
    private val canciones: List<Song>,
    private val onClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.cancion_item)
        val ivCover: ImageView = view.findViewById(R.id.ivCancionCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cancion, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = canciones[position]
        holder.titulo.text = "${song.titulo} - ${song.artista}"

        // carga de las portadas en la lista
        try {
            val inputStream = holder.itemView.context.assets.open("Songs/${song.portada}")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            holder.ivCover.setImageBitmap(bitmap)
        } catch (_: IOException) {
            // si no encuentra la ruta pone la imagen por defecto
            holder.ivCover.setImageResource(R.drawable.portada)
        }

        holder.itemView.setOnClickListener { onClick(song) }
    }

    override fun getItemCount() = canciones.size
}