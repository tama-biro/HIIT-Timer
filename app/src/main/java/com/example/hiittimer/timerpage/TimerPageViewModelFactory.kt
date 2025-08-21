package com.example.hiittimer.timerpage

import com.example.hiittimer.TimerSettings
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimerPageViewModelFactory(
    private val application: Application,
    private val settings: TimerSettings
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerPageViewModel(application, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
