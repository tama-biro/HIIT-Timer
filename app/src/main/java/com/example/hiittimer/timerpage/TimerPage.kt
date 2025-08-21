package com.example.hiittimer.timerpage

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hiittimer.TimerSettings
import kotlinx.coroutines.delay
import kotlin.concurrent.fixedRateTimer
import java.util.Locale

/**
 * A data class to hold all the timer settings from the landing page.
 * This is how you would pass the values from one screen to the next.
 */

enum class TimerMode { PREP, WORK, REST, FINISHED }

/**
 * Main Composable for the HIIT timer page.
 * It manages the countdown logic, mode switching, and UI state.
 *
 * @param settings The timer settings from the previous screen.
 * @param onReset A callback function to be executed when the user confirms a reset,
 * which would typically navigate back to the landing page.
 */
@Composable
fun TimerPage(
    settings: TimerSettings,
    onReset: () -> Unit,
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: TimerPageViewModel = viewModel(
        factory = TimerPageViewModelFactory(application, settings)
    )

    val dynamicColor = when (viewModel.currentMode) {
        TimerMode.PREP -> MaterialTheme.colorScheme.surface
        TimerMode.WORK -> Color(0xFF7FF160)
        TimerMode.REST -> Color(0xFFC42ECC)
        TimerMode.FINISHED -> MaterialTheme.colorScheme.surface
    }

    LaunchedEffect(viewModel.isRunning) {
        if (viewModel.isRunning) {
            val mainHandler = Handler(Looper.getMainLooper())

            val timer = fixedRateTimer(
                name = "hiit-timer",
                initialDelay = 1000L,
                period = 1000L
            ) {
                mainHandler.post {
                    if (!viewModel.isRunning) {
                        cancel()
                    } else {
                        if (viewModel.timeRemaining > 0) {
                            if (viewModel.timeRemaining == 1) {
                                viewModel.playSwitchSound()
                            } else if (viewModel.timeRemaining < 5) {
                                viewModel.playCountdownSound()
                            }
                            viewModel.reduceTimer()
                            if (viewModel.currentMode != TimerMode.PREP) {
                                viewModel.addTotalTime()
                            }
                        } else {
                            when (viewModel.currentMode) {
                                TimerMode.PREP -> {
                                    viewModel.setCurrentModeAndTimeRemaining(
                                        TimerMode.WORK,
                                        settings.workSeconds
                                    )
                                }
                                TimerMode.WORK -> {
                                    if (viewModel.currentRound < settings.rounds) {
                                        viewModel.setCurrentModeAndTimeRemaining(
                                            TimerMode.REST,
                                            settings.restSeconds
                                        )
                                    } else {
                                        viewModel.setCurrentModeAndTimeRemaining(
                                            TimerMode.FINISHED,
                                            0
                                        )
                                        viewModel.onToggleFinish()
                                        viewModel.playFinishSound()
                                        cancel()
                                    }
                                }
                                TimerMode.REST -> {
                                    viewModel.setCurrentModeAndTimeRemaining(
                                        TimerMode.WORK,
                                        settings.workSeconds
                                    )
                                    viewModel.addRoundCount()
                                }
                                TimerMode.FINISHED -> {
                                    viewModel.onToggleFinish()
                                    cancel()
                                }
                            }
                        }
                    }
                }
            }

            try {
                while (viewModel.isRunning) {
                    delay(1000L)
                }
            } finally {
                timer.cancel()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = dynamicColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDisplayRow(label = "Mode", value = viewModel.currentMode.name, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.timeRemaining / 60, viewModel.timeRemaining % 60),
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TimerDisplayRow(label = "Round", value = "${viewModel.currentRound} / ${settings.rounds}")
        Spacer(modifier = Modifier.height(16.dp))

        TimerDisplayRow(label = "Total Elapsed", value = String.format(Locale.getDefault(), "%02d:%02d", viewModel.totalElapsedTime / 60, viewModel.totalElapsedTime % 60))

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.onToggleVolume() }) {
                Icon(
                    imageVector = if (viewModel.isVolumeOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = "Toggle Volume",
                    modifier = Modifier.size(48.dp)
                )
            }

            Button(
                onClick = { viewModel.onTogglePlay() },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Start/Pause",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { viewModel.onShowResetDialog() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }

    if (viewModel.isResetDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onDisableResetDialog() },
            title = { Text(text = "Are you sure?") },
            text = { Text(text = "Do you want to reset the timers and go back to the setup page?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onDisableResetDialog()
                        onReset()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.onDisableResetDialog() }) {
                    Text("No")
                }
            }
        )
    }
}

/**
 * A reusable composable for a label and value row.
 */
@Composable
fun TimerDisplayRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.primary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// Preview function with some sample settings
@Preview(showBackground = true)
@Composable
fun TimerPagePreview() {
    MaterialTheme {
        TimerPage(
            settings = TimerSettings(
                prepSeconds = 10,
                workSeconds = 30,
                restSeconds = 30,
                rounds = 10
            ),
            onReset = {}
        )
    }
}
