package com.example.hiittimer

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
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
fun TimerPage(settings: TimerSettings, onReset: () -> Unit) {
    // State variables for the timer
    var isRunning by remember { mutableStateOf(false) }
    var totalElapsedTime by remember { mutableIntStateOf(0) }
    var currentRound by remember { mutableIntStateOf(1) }
    var currentMode by remember { mutableStateOf(TimerMode.PREP) }
    var timeRemaining by remember { mutableIntStateOf(settings.prepSeconds) }
    var isResetDialogVisible by remember { mutableStateOf(false) }
    var isVolumeOn by remember { mutableStateOf(true) }

    val dynamicColor = when (currentMode) {
        TimerMode.PREP -> MaterialTheme.colorScheme.surface
        TimerMode.WORK -> Color(0xFFC42ECC)
        TimerMode.REST -> Color(0xFF7FF160)
        TimerMode.FINISHED -> MaterialTheme.colorScheme.surface
    }

    val context = LocalContext.current
    val soundPlayer = remember { SoundPlayer(context) }
    val volume = if (isVolumeOn) 1.0f else 0f

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val mainHandler = Handler(Looper.getMainLooper())

            val timer = fixedRateTimer(
                name = "hiit-timer",
                initialDelay = 1000L,
                period = 1000L
            ) {
                mainHandler.post {
                    if (!isRunning) {
                        cancel()
                    } else {
                        if (timeRemaining > 0) {
                            if (timeRemaining == 1) {
                                soundPlayer.playSwitch(volume)
                            } else if (timeRemaining < 5) {
                                soundPlayer.playCountdown(volume)
                            }
                            timeRemaining--
                            if (currentMode != TimerMode.PREP) {
                                totalElapsedTime++
                            }
                        } else {
                            when (currentMode) {
                                TimerMode.PREP -> {
                                    currentMode = TimerMode.WORK
                                    timeRemaining = settings.workSeconds
                                }
                                TimerMode.WORK -> {
                                    if (currentRound < settings.rounds) {
                                        currentMode = TimerMode.REST
                                        timeRemaining = settings.restSeconds
                                    } else {
                                        currentMode = TimerMode.FINISHED
                                        isRunning = false
                                        soundPlayer.playFinish(volume)
                                        cancel()
                                    }
                                }
                                TimerMode.REST -> {
                                    currentMode = TimerMode.WORK
                                    timeRemaining = settings.workSeconds
                                    currentRound++
                                }
                                TimerMode.FINISHED -> {
                                    isRunning = false
                                    cancel()
                                }
                            }
                        }
                    }
                }
            }

            try {
                // keep coroutine alive while timer runs
                while (isRunning) {
                    kotlinx.coroutines.delay(1000L)
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
        TimerDisplayRow(label = "Mode", value = currentMode.name, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format(Locale.getDefault(), "%02d:%02d", timeRemaining / 60, timeRemaining % 60),
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TimerDisplayRow(label = "Round", value = "$currentRound / ${settings.rounds}")
        Spacer(modifier = Modifier.height(16.dp))

        TimerDisplayRow(label = "Total Elapsed", value = String.format(Locale.getDefault(), "%02d:%02d", totalElapsedTime / 60, totalElapsedTime % 60))

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isVolumeOn = !isVolumeOn }) {
                Icon(
                    imageVector = if (isVolumeOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = "Toggle Volume",
                    modifier = Modifier.size(48.dp)
                )
            }

            Button(
                onClick = { isRunning = !isRunning },
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
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Start/Pause",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Reset Button
            IconButton(onClick = { isResetDialogVisible = true }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }

    if (isResetDialogVisible) {
        AlertDialog(
            onDismissRequest = { isResetDialogVisible = false },
            title = { Text(text = "Are you sure?") },
            text = { Text(text = "Do you want to reset the timers and go back to the setup page?") },
            confirmButton = {
                Button(
                    onClick = {
                        isResetDialogVisible = false
                        onReset() // Call the reset callback
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { isResetDialogVisible = false }) {
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
