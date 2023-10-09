package com.example.exovideoplayer

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer

object ExoPlayerSingleton {
    private var player: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .build()
        }
        return player as ExoPlayer
    }
}
