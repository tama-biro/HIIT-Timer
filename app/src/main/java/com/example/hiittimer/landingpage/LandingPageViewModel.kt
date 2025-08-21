package com.example.hiittimer.landingpage

import com.example.hiittimer.TimerSettings
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*

class LandingPageViewModel : ViewModel() {
    var prepTimeMinutes by mutableIntStateOf(0)
    var prepTimeSeconds by mutableIntStateOf(10)

    var workTimeMinutes by mutableIntStateOf(0)
    var workTimeSeconds by mutableIntStateOf(30)

    var restTimeMinutes by mutableIntStateOf(0)
    var restTimeSeconds by mutableIntStateOf(30)

    var rounds by mutableIntStateOf(10)

    val totalTimeInSeconds: State<Int> = derivedStateOf {
        val work = workTimeMinutes * 60 + workTimeSeconds
        val rest = restTimeMinutes * 60 + restTimeSeconds
        rounds * (work + rest)
    }

    fun onStart(onStart: (TimerSettings) -> Unit) {
        val settings = TimerSettings(
            prepSeconds = prepTimeMinutes * 60 + prepTimeSeconds,
            workSeconds = workTimeMinutes * 60 + workTimeSeconds,
            restSeconds = restTimeMinutes * 60 + restTimeSeconds,
            rounds = rounds
        )
        onStart(settings)
    }

    fun onReset() {
        prepTimeMinutes = 0; prepTimeSeconds = 10
        workTimeMinutes = 0; workTimeSeconds = 30
        restTimeMinutes = 0; restTimeSeconds = 30
        rounds = 30
    }
}
