package com.example.demo_list_v2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException


class PlaylistFragment : Fragment() {

    private var isAscending = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Canciones.cargarDesdeJson(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSongs)
        val btnSort = view.findViewById<Button>(R.id.btnSort)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        val adapter = SongAdapter(Canciones.lista) { song ->
            val fragmentDetail = Song_detail.newInstance(song)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentDetail)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        btnSort.setOnClickListener {
            if (isAscending) {
                Canciones.lista.sortByDescending { it.titulo.lowercase() }
                btnSort.text = "Ordenar A-Z"
                isAscending = false
            } else {
                Canciones.lista.sortBy { it.titulo.lowercase() }
                btnSort.text = "Ordenar Z-A"
                isAscending = true
            }
            adapter.notifyDataSetChanged()
        }
    }
}

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