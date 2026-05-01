package com.example.demo_list_v2

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

object MusicPlayerManager {

    private var player: ExoPlayer? = null
    var currentSong: Song? = null
        private set

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext).build()
        }
        return player!!
    }

    fun playSong(context: Context, song: Song, playWhenReady: Boolean = true) {
        val exoPlayer = getPlayer(context)

        currentSong = song

        val audioUri = Uri.parse("asset:///Songs/${song.archivo}")
        val mediaItem = MediaItem.fromUri(audioUri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = playWhenReady
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return player?.duration ?: 0L
    }

    fun playNext(context: Context): Song? {
        val current = currentSong ?: return null
        val index = Canciones.lista.indexOfFirst { it.id == current.id }

        if (index == -1) return null

        val nextIndex = if (index + 1 < Canciones.lista.size) index + 1 else 0
        val nextSong = Canciones.lista[nextIndex]

        playSong(context, nextSong)
        return nextSong
    }

    fun playPrevious(context: Context): Song? {
        val current = currentSong ?: return null
        val index = Canciones.lista.indexOfFirst { it.id == current.id }

        if (index == -1) return null

        val previousIndex = if (index - 1 >= 0) index - 1 else Canciones.lista.size - 1
        val previousSong = Canciones.lista[previousIndex]

        playSong(context, previousSong)
        return previousSong
    }

    fun release() {
        player?.release()
        player = null
        currentSong = null
    }
}
