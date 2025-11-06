// nav/Route.kt
package com.example.timecapsule.nav
sealed interface Route {
    companion object {
        const val Home = "home"
        const val List = "list"
        const val Write = "write"
        const val Settings = "settings"
    }
}