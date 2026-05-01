package com.example.demo_list_v2

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.io.InputStreamReader

object Canciones {
    var lista: MutableList<Song> = mutableListOf()

    fun cargarDesdeJson(context: Context) {
        if (lista.isNotEmpty()) return
        
        try {
            val inputStream = context.assets.open("Songs/songs.json")
            val reader = InputStreamReader(inputStream)
            val jsonString = reader.readText()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val song = Song(
                    id = obj.getInt("id"),
                    titulo = obj.getString("titulo"),
                    artista = obj.getString("artista"),
                    album = obj.getString("album"),
                    genero = obj.getString("genero"),
                    duracion = obj.getString("duracion"),
                    fecha_lanzamiento = obj.getString("fecha_lanzamiento"),
                    portada = obj.getString("portada"),
                    archivo = obj.getString("archivo")
                )
                lista.add(song)
            }
            reader.close()
            Log.d("Canciones", "Cargadas ${lista.size} canciones")
        } catch (e: Exception) {
            Log.e("Canciones", "Error cargando JSON", e)
        }
    }
}