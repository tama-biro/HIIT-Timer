package com.example.hiittimer.timerpage

import com.example.hiittimer.TimerSettings
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.*

class TimerPageViewModel(
    application: Application,
    settings: TimerSettings
) : AndroidViewModel(application) {
    var isRunning by mutableStateOf(false)
    var totalElapsedTime by mutableIntStateOf(0)
    var currentRound by mutableIntStateOf(1)
    var currentMode by mutableStateOf(TimerMode.PREP)
    var timeRemaining by mutableIntStateOf(settings.prepSeconds)
    var isResetDialogVisible by mutableStateOf(false)
    var isVolumeOn by mutableStateOf(true)

    val soundPlayer = SoundPlayer(application.applicationContext)
    val volume = if (isVolumeOn) 1.0f else 0f

    fun reduceTimer() {
        timeRemaining--
    }

    fun addRoundCount() {
        currentRound++
    }

    fun addTotalTime() {
        totalElapsedTime++
    }

    fun setCurrentModeAndTimeRemaining(
        modeToSet: TimerMode,
        timeToSet: Int
    ) {
        currentMode = modeToSet
        timeRemaining = timeToSet
    }

    fun onToggleVolume() {
        isVolumeOn = !isVolumeOn
    }

    fun onTogglePlay() {
        isRunning = !isRunning
    }

    fun onToggleFinish() {
        isRunning = false
    }

    fun onShowResetDialog() {
        isResetDialogVisible = true
    }

    fun onDisableResetDialog() {
        isResetDialogVisible = false
    }

    fun playCountdownSound() {
        if (isVolumeOn) {
            soundPlayer.playCountdown(volume)
        }
    }

    fun playSwitchSound() {
        if (isVolumeOn) {
            soundPlayer.playSwitch(volume)
        }
    }

    fun playFinishSound() {
        if (isVolumeOn) {
            soundPlayer.playFinish(volume)
        }
    }
}
