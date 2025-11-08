// MainActivity.kt
package com.example.timecapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.timecapsule.NavigationLimiter
import com.example.timecapsule.nav.Route
import com.example.timecapsule.ui.theme.TimeCapsuleTheme
import com.example.timecapsule.ui.screens.HomeScreen
import com.example.timecapsule.ui.screens.ListScreen
import com.example.timecapsule.ui.screens.EditScreen
import com.example.timecapsule.ui.screens.WriteScreen
import com.example.timecapsule.ui.screens.SettingsScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        setContent {
            TimeCapsuleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val nav = rememberNavController()
                    val navLimiter = remember(nav) { NavigationLimiter(nav) }
                    val pending by navLimiter.pendingNavigation.collectAsState()

                    BackHandler(enabled = pending != null) {}

                    LaunchedEffect(pending?.token) {
                        val token = pending?.token ?: return@LaunchedEffect
                        delay(2000)
                        if (navLimiter.isPending(token)) {
                            navLimiter.navigate(Route.Home, inclusiveStart = true, force = true)
                            navLimiter.clearPending(token)
                        }
                    }
                    Box(Modifier.fillMaxSize()) {
                        NavHost(
                            navController = nav,
                            startDestination = Route.Home,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition = { ExitTransition.None }
                        ) {
                            composable(Route.Home) {
                                HomeScreen(
                                    onScreenReady = { navLimiter.markReady(Route.Home) },
                                    onOpenList = {
                                        navLimiter.navigate(Route.List)
                                    },
                                    onOpenWrite = {
                                        navLimiter.navigate(Route.Write)
                                    },
                                    onOpenSettings = { navLimiter.navigate(Route.Settings) },
                                    onBackToHome = {
                                        navLimiter.navigate(Route.Home, inclusiveStart = true)
                                    }
                                )
                            }
                            composable(Route.List) {
                                ListScreen(
                                    onBack = {
                                        navLimiter.popBackStack()
                                    },
                                    onOpenWrite = {
                                        navLimiter.navigate(Route.Write)
                                    },
                                    onOpenEdit = { id ->
                                        navLimiter.navigate("${Route.Edit}/$id")
                                    },
                                    onScreenReady = { navLimiter.markReady(Route.List) }
                                )
                            }
                            composable(Route.Write) {
                                WriteScreen(
                                    onSaved = {
                                        navLimiter.navigate(Route.Home, inclusiveStart = true)
                                    },
                                    onCancel = {
                                        navLimiter.navigate(
                                            Route.Home,
                                            inclusiveStart = true,
                                            force = true
                                        )
                                    },
                                    onReady = { navLimiter.markReady(Route.Write) }
                                )
                            }
                            composable(Route.Settings) {
                                SettingsScreen(
                                    onBack = {
                                        navLimiter.popBackStack()
                                    },
                                    onReady = { navLimiter.markReady(Route.Settings) }
                                )
                            }
                            composable("${Route.Edit}/{docId}") { backStackEntry ->
                                val docId = backStackEntry.arguments?.getString("docId")
                                    ?: return@composable
                                EditScreen(
                                    docId = docId,
                                    onClose = { navLimiter.popBackStack() },
                                    onReady = { navLimiter.markReady("${Route.Edit}/$docId") }
                                )
                            }
                        }
                        ScreenTransitionBlocker(isBlocking = pending != null)
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    @Composable
    private fun ScreenTransitionBlocker(isBlocking: Boolean) {
        if (!isBlocking) return
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.12f))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
