package com.example.privatechat.data.repository

import android.content.Context
import android.media.AudioManager
import android.util.Log

/**
 * Handles WebRTC / Audio-Video call signaling and device audio routing.
 */
class WebRtcClient(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var isSpeakerOn = false

    fun initAudio() {
        try {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager.isSpeakerphoneOn = true
            isSpeakerOn = true
        } catch (e: Exception) {
            Log.e("WebRtcClient", "Error initializing audio", e)
        }
    }

    fun toggleSpeaker(): Boolean {
        isSpeakerOn = !isSpeakerOn
        audioManager.isSpeakerphoneOn = isSpeakerOn
        return isSpeakerOn
    }

    fun releaseAudio() {
        try {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = false
        } catch (e: Exception) {
            Log.e("WebRtcClient", "Error releasing audio", e)
        }
    }
}
