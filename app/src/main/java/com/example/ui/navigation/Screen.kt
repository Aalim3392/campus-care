package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Support
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("home", "Home", Icons.Default.GridView)
    data object DailyCheckIn : Screen("daily-check-in", "Check-in", Icons.Default.CheckCircle)
    data object Progress : Screen("my-progress", "Progress", Icons.AutoMirrored.Filled.TrendingUp)
    data object Support : Screen("support", "Support", Icons.Default.Support)
    data object Meditation : Screen("meditation", "Meditation", Icons.Default.SelfImprovement)

    companion object {
        // Essential mobile bottom navigation items only
        val bottomNavItems: List<Screen>
            get() = listOf(Dashboard, DailyCheckIn, Progress, Support)
    }
}
