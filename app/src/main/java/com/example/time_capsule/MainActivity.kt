// MainActivity.kt
package com.example.timecapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.time_capsule.nav.Route
import com.example.time_capsule.ui.theme.TimeCapsuleTheme
import com.example.time_capsule.ui.screens.HomeScreen
import com.example.time_capsule.ui.screens.ListScreen
import com.example.time_capsule.ui.screens.WriteScreen
import com.example.time_capsule.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeCapsuleTheme {
                val nav = rememberNavController()
                NavHost(navController = nav, startDestination = Route.Home) {
                    composable(Route.Home) {
                        HomeScreen(
                            onOpenList = { nav.navigate(Route.List) },
                            onOpenWrite = { nav.navigate(Route.Write) },
                            onOpenSettings = { nav.navigate(Route.Settings) }
                        )
                    }
                    composable(Route.List) {
                        ListScreen(
                            onBack = { nav.popBackStack() },
                            onOpenWrite = { nav.navigate(Route.Write) }
                        )
                    }
                    composable(Route.Write) {
                        WriteScreen(
                            onSaved = { nav.popBackStack() },
                            onCancel = {
                                nav.navigate(Route.Home) {
                                    popUpTo(Route.Home) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Route.Settings) {
                        SettingsScreen(onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
