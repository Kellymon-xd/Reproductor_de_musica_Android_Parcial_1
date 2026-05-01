package com.example.demo_list_v2

import java.io.Serializable

data class Song(
    val id: Int,
    val titulo: String,
    val artista: String,
    val album: String,
    val genero: String,
    val duracion: String,
    val fecha_lanzamiento: String,
    val portada: String,
    val archivo: String
) : Serializable