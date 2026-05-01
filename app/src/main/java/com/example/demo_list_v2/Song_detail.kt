package com.example.demo_list_v2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import java.io.IOException

private const val ARG_SONG = "song"

class Song_detail : Fragment() {

    private var song: Song? = null

    private val handler = Handler(Looper.getMainLooper())
    private var usuarioMoviendoBarra = false

    private lateinit var tvTitulo: TextView
    private lateinit var tvArtista: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvDuracion: TextView
    private lateinit var tvFecha: TextView
    private lateinit var ivCover: ImageView

    private lateinit var seekBarAudio: SeekBar
    private lateinit var tvTiempoActual: TextView
    private lateinit var tvTiempoTotal: TextView
    private lateinit var btnPlayPause: Button
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            song = it.getSerializable(ARG_SONG) as? Song
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_song_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enlazarVistas(view)

        val selectedSong = song ?: return
        val cancionParaMostrar: Song

        if (savedInstanceState == null) {
            // CASO: Entrada nueva desde la lista
            cancionParaMostrar = selectedSong
            
            // Si el reproductor tiene otra canción o está vacío, cargamos la nueva (en pausa)
            if (MusicPlayerManager.currentSong?.id != selectedSong.id) {
                MusicPlayerManager.playSong(requireContext(), selectedSong, false)
            }
        } else {
            // CASO: Recreación por rotación
            // Mantenemos lo que el manager ya esté gestionando (por si se cambió de canción dentro del player)
            cancionParaMostrar = MusicPlayerManager.currentSong ?: selectedSong
        }

        mostrarCancion(cancionParaMostrar)

        actualizarBotonPlayPause()
        configurarBotones()
        configurarSeekBar()
        actualizarProgreso()
    }

    private fun enlazarVistas(view: View) {
        tvTitulo = view.findViewById(R.id.tvDetalleTitulo)
        tvArtista = view.findViewById(R.id.tvDetalleArtista)
        tvAlbum = view.findViewById(R.id.tvDetalleAlbum)
        tvGenero = view.findViewById(R.id.tvDetalleGenero)
        tvDuracion = view.findViewById(R.id.tvDetalleDuracion)
        tvFecha = view.findViewById(R.id.tvDetalleFecha)
        ivCover = view.findViewById(R.id.ivDetalleCover)

        seekBarAudio = view.findViewById(R.id.seekBarAudio)
        tvTiempoActual = view.findViewById(R.id.tvTiempoActual)
        tvTiempoTotal = view.findViewById(R.id.tvTiempoTotal)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
    }

    private fun mostrarCancion(song: Song) {
        this.song = song

        tvTitulo.text = song.titulo
        tvArtista.text = song.artista
        tvAlbum.text = song.album
        tvGenero.text = song.genero
        tvDuracion.text = song.duracion
        tvFecha.text = "Lanzamiento: ${song.fecha_lanzamiento}"

        tvTiempoTotal.text = song.duracion

        try {
            val inputStream = requireContext().assets.open("Songs/${song.portada}")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            ivCover.setImageBitmap(bitmap)
        } catch (e: IOException) {
            ivCover.setImageResource(R.drawable.portada)
        }
    }

    private fun configurarBotones() {
        btnPlayPause.setOnClickListener {
            if (MusicPlayerManager.isPlaying()) {
                MusicPlayerManager.pause()
                btnPlayPause.text = "▶"
            } else {
                MusicPlayerManager.play()
                btnPlayPause.text = "⏸"
            }
        }

        btnAnterior.setOnClickListener {
            val previousSong = MusicPlayerManager.playPrevious(requireContext())

            previousSong?.let {
                mostrarCancion(it)
                seekBarAudio.progress = 0
                tvTiempoActual.text = "0:00"
                btnPlayPause.text = "⏸"
            }
        }

        btnSiguiente.setOnClickListener {
            val nextSong = MusicPlayerManager.playNext(requireContext())

            nextSong?.let {
                mostrarCancion(it)
                seekBarAudio.progress = 0
                tvTiempoActual.text = "0:00"
                btnPlayPause.text = "⏸"
            }
        }
    }

    private fun configurarSeekBar() {
        seekBarAudio.max = 1000

        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                if (fromUser) {
                    val duration = MusicPlayerManager.getDuration()

                    if (duration > 0) {
                        val nuevaPosicion = duration * progress / 1000
                        tvTiempoActual.text = formatearTiempo(nuevaPosicion)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                usuarioMoviendoBarra = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val duration = MusicPlayerManager.getDuration()
                val progress = seekBar?.progress ?: 0

                if (duration > 0) {
                    val nuevaPosicion = duration * progress / 1000
                    MusicPlayerManager.seekTo(nuevaPosicion)
                }

                usuarioMoviendoBarra = false
            }
        })
    }

    private fun actualizarProgreso() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val current = MusicPlayerManager.getCurrentPosition()
                val duration = MusicPlayerManager.getDuration()

                if (duration > 0 && !usuarioMoviendoBarra) {
                    val progress = ((current * 1000) / duration).toInt()

                    seekBarAudio.progress = progress
                    tvTiempoActual.text = formatearTiempo(current)
                    tvTiempoTotal.text = formatearTiempo(duration)
                }

                actualizarBotonPlayPause()

                handler.postDelayed(this, 500)
            }
        }, 500)
    }

    private fun actualizarBotonPlayPause() {
        if (MusicPlayerManager.isPlaying()) {
            btnPlayPause.text = "⏸"
        } else {
            btnPlayPause.text = "▶"
        }
    }

    private fun formatearTiempo(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        @JvmStatic
        fun newInstance(song: Song) =
            Song_detail().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SONG, song)
                }
            }
    }
}
