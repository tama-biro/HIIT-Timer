package com.example.hiittimer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.hiittimer.ui.theme.HIITTimerTheme

/**
 * Main Composable function for the timer landing page.
 * It now takes an `onStart` callback to handle navigation.
 *
 * @param onStart A function that is called with the settings when the user clicks 'Start'.
 */
@Composable
fun TimerLandingPage(onStart: (TimerSettings) -> Unit) {
    var prepTimeMinutes by remember { mutableIntStateOf(0) }
    var prepTimeSeconds by remember { mutableIntStateOf(10) }

    var workTimeMinutes by remember { mutableIntStateOf(0) }
    var workTimeSeconds by remember { mutableIntStateOf(30) }

    var restTimeMinutes by remember { mutableIntStateOf(0) }
    var restTimeSeconds by remember { mutableIntStateOf(30) }

    var rounds by remember { mutableIntStateOf(10) }

    // Calculate total time whenever any of the state variables change
    val totalTimeInSeconds by remember {
        derivedStateOf {
            val work = workTimeMinutes * 60 + workTimeSeconds
            val rest = restTimeMinutes * 60 + restTimeSeconds
            rounds * (work + rest)
        }
    }

    // Main layout using a Column to stack elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // Time selection rows
        TimeSettingRow(
            label = "Prep",
            minutes = prepTimeMinutes,
            onMinutesChange = { prepTimeMinutes = it },
            seconds = prepTimeSeconds,
            onSecondsChange = { prepTimeSeconds = it }
        )
        TimeSettingRow(
            label = "Work",
            minutes = workTimeMinutes,
            onMinutesChange = { workTimeMinutes = it },
            seconds = workTimeSeconds,
            onSecondsChange = { workTimeSeconds = it }
        )
        TimeSettingRow(
            label = "Rest",
            minutes = restTimeMinutes,
            onMinutesChange = { restTimeMinutes = it },
            seconds = restTimeSeconds,
            onSecondsChange = { restTimeSeconds = it }
        )

        // Rounds selection row
        RoundsSettingRow(
            label = "Rounds",
            rounds = rounds,
            onRoundsChange = { rounds = it }
        )

        // Total time display
        TotalTimeDisplay(totalTimeInSeconds)

        Spacer(modifier = Modifier.height(16.dp))

        // Start and Reset buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // Call the onStart callback with the current settings
                    val settings = TimerSettings(
                        prepSeconds = prepTimeMinutes * 60 + prepTimeSeconds,
                        workSeconds = workTimeMinutes * 60 + workTimeSeconds,
                        restSeconds = restTimeMinutes * 60 + restTimeSeconds,
                        rounds = rounds
                    )
                    onStart(settings)
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Start", fontSize = 18.sp)
            }
            Button(
                onClick = {
                    // Reset all state to initial values
                    prepTimeMinutes = 0; prepTimeSeconds = 10
                    workTimeMinutes = 0; workTimeSeconds = 30
                    restTimeMinutes = 0; restTimeSeconds = 30
                    rounds = 30
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Reset", fontSize = 18.sp)
            }
        }
    }
}

/**
 * Reusable Composable for a single row with a label and time pickers.
 * A simplified "picker" is created with Text and increment/decrement buttons.
 */
@Composable
fun TimeSettingRow(
    label: String,
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    seconds: Int,
    onSecondsChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label on the left
        Text(text = label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        // Time selector on the right
        Row(verticalAlignment = Alignment.CenterVertically) {
            NumberPicker(value = minutes, onValueChange = onMinutesChange, label = "m")
            Spacer(modifier = Modifier.width(8.dp))
            NumberPicker(value = seconds, onValueChange = onSecondsChange, label = "s", max = 59)
        }
    }
}

/**
 * Reusable Composable for a single row with a label and rounds picker.
 */
@Composable
fun RoundsSettingRow(
    label: String,
    rounds: Int,
    onRoundsChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        NumberPicker(value = rounds, onValueChange = onRoundsChange, label = "rounds", min = 1, max = 99)
    }
}

/**
 * Displays the total calculated time.
 */
@Composable
fun TotalTimeDisplay(totalTimeInSeconds: Int) {
    val totalMinutes = totalTimeInSeconds / 60
    val totalSeconds = totalTimeInSeconds % 60
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Total Time", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        Text(
            text = String.format(Locale.getDefault(), "%d min %02d sec", totalMinutes, totalSeconds),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Preview function for the Composable
@Preview(showBackground = true)
@Composable
fun TimerLandingPagePreview() {
    HIITTimerTheme {
        TimerLandingPage(onStart = {})
    }
}
