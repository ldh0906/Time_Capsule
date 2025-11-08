package com.example.timecapsule

import android.os.SystemClock
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.timecapsule.nav.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NavigationLimiter(
    private val navController: NavHostController,
    private val maxStackDepth: Int = 6,
    private val minIntervalMs: Long = 400
) {
    data class PendingNavigation(val route: String, val startedAt: Long, val token: Long)

    private var lastNavigateAt = 0L
    private val _pendingNavigation = MutableStateFlow<PendingNavigation?>(null)
    val pendingNavigation: StateFlow<PendingNavigation?> = _pendingNavigation

    private fun currentTime() = SystemClock.elapsedRealtime()

    private fun shouldThrottle(force: Boolean): Boolean {
        if (force) return false
        val now = currentTime()
        val tooFast = now - lastNavigateAt < minIntervalMs
        if (!tooFast) {
            lastNavigateAt = now
        }
        return tooFast
    }

    private fun enforceStackLimit() {
        val entryCount = navController.backQueue.count { it.destination.route != null }
        if (entryCount > maxStackDepth) {
            navController.navigateSingleTop(Route.Home, inclusiveStart = true)
            lastNavigateAt = currentTime()
        }
    }

    fun navigate(route: String, inclusiveStart: Boolean = false, force: Boolean = false) {
        if (shouldThrottle(force)) return
        if (!force) enforceStackLimit()
        navController.navigateSingleTop(route, inclusiveStart)
        lastNavigateAt = currentTime()
        _pendingNavigation.value = PendingNavigation(route, lastNavigateAt, lastNavigateAt)
    }

    fun popBackStack(force: Boolean = false): Boolean {
        if (shouldThrottle(force)) return false
        val targetRoute = navController.previousBackStackEntry?.destination?.route
        val popped = navController.popBackStack()
        if (popped) {
            lastNavigateAt = currentTime()
        }
        if (!force) enforceStackLimit()
        if (popped) {
            if (targetRoute != null) {
                _pendingNavigation.value = PendingNavigation(targetRoute, lastNavigateAt, lastNavigateAt)
            } else {
                _pendingNavigation.value = null
            }
        }
        return popped
    }

    fun isPending(token: Long): Boolean =
        _pendingNavigation.value?.token == token

    fun clearPending(token: Long) {
        _pendingNavigation.update { current ->
            if (current?.token == token) null else current
        }
    }

    fun markReady(route: String) {
        _pendingNavigation.update { current ->
            if (current?.route == route) null else current
        }
    }
}

fun NavHostController.navigateSingleTop(route: String, inclusiveStart: Boolean = false) {
    if (!inclusiveStart && currentBackStackEntry?.destination?.route == route) return
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = inclusiveStart
        }
        launchSingleTop = true
    }
}
