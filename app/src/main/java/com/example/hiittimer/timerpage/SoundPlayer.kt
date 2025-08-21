package com.example.hiittimer.timerpage

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.hiittimer.R

class SoundPlayer(private val context: Context) {
    private val soundPool: SoundPool
    private val countdownSoundId: Int
    private val switchSoundId: Int
    private val finishSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        countdownSoundId = soundPool.load(context, R.raw.beep_sound, 1)
        switchSoundId = soundPool.load(context, R.raw.wee_sound, 1)
        finishSoundId = soundPool.load(context, R.raw.yaas_sound, 1)
    }

    fun setVolume(volume: Float) {

    }

    fun playCountdown(volume: Float) {
        soundPool.play(countdownSoundId, volume, volume, 1, 0, 1.0f)
    }

    fun playSwitch(volume: Float) {
        soundPool.play(switchSoundId, volume, volume, 1, 0, 1.0f)
    }

    fun playFinish(volume: Float) {
        soundPool.play(finishSoundId, volume, volume, 1, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }
}
