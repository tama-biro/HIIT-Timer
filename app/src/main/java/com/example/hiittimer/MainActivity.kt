package com.example.hiittimer

import com.example.hiittimer.ui.theme.HIITTimerTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

data class TimerSettings(
    val prepSeconds: Int,
    val workSeconds: Int,
    val restSeconds: Int,
    val rounds: Int
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HIITTimerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") {
                        TimerLandingPage(onStart = { settings ->
                            navController.navigate("timer/${settings.prepSeconds}/${settings.workSeconds}/${settings.restSeconds}/${settings.rounds}")
                        })
                    }
                    composable(
                        "timer/{prepSeconds}/{workSeconds}/{restSeconds}/{rounds}",
                        arguments = listOf(
                            navArgument("prepSeconds") { type = NavType.IntType },
                            navArgument("workSeconds") { type = NavType.IntType },
                            navArgument("restSeconds") { type = NavType.IntType },
                            navArgument("rounds") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val prep = backStackEntry.arguments?.getInt("prepSeconds") ?: 0
                        val work = backStackEntry.arguments?.getInt("workSeconds") ?: 0
                        val rest = backStackEntry.arguments?.getInt("restSeconds") ?: 0
                        val rounds = backStackEntry.arguments?.getInt("rounds") ?: 0

                        val settings = TimerSettings(prep, work, rest, rounds)

                        TimerPage(settings = settings, onReset = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}
