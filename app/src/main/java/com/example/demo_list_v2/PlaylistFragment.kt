package com.example.demo_list_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


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