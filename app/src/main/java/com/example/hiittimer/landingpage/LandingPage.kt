package com.example.hiittimer.landingpage

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
import com.example.hiittimer.TimerSettings
import com.example.hiittimer.ui.theme.HIITTimerTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun LandingPage(
    viewModel: LandingPageViewModel = viewModel(),
    onStart: (TimerSettings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        TimeSettingRow(
            label = "Prep",
            minutes = viewModel.prepTimeMinutes,
            onMinutesChange = { viewModel.prepTimeMinutes = it },
            seconds = viewModel.prepTimeSeconds,
            onSecondsChange = { viewModel.prepTimeSeconds = it }
        )
        TimeSettingRow(
            label = "Work",
            minutes = viewModel.workTimeMinutes,
            onMinutesChange = { viewModel.workTimeMinutes = it },
            seconds = viewModel.workTimeSeconds,
            onSecondsChange = { viewModel.workTimeSeconds = it }
        )
        TimeSettingRow(
            label = "Rest",
            minutes = viewModel.restTimeMinutes,
            onMinutesChange = { viewModel.restTimeMinutes = it },
            seconds = viewModel.restTimeSeconds,
            onSecondsChange = { viewModel.restTimeSeconds = it }
        )

        RoundsSettingRow(
            label = "Rounds",
            rounds = viewModel.rounds,
            onRoundsChange = { viewModel.rounds = it }
        )

        TotalTimeDisplay(viewModel.totalTimeInSeconds.value)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val settings = TimerSettings(
                        prepSeconds = viewModel.prepTimeMinutes * 60 + viewModel.prepTimeSeconds,
                        workSeconds = viewModel.workTimeMinutes * 60 + viewModel.workTimeSeconds,
                        restSeconds = viewModel.restTimeMinutes * 60 + viewModel.restTimeSeconds,
                        rounds = viewModel.rounds
                    )
                    viewModel.onStart { onStart(settings) }
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
                onClick = { viewModel.onReset() },
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
        Text(text = label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

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

@Preview(showBackground = true)
@Composable
fun LandingPagePreview() {
    HIITTimerTheme {
        LandingPage(onStart = {})
    }
}
