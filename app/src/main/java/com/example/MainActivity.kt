package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusWellViewModel
import com.example.ui.components.CampusWellHeader
import com.example.ui.components.ProfileDialog
import com.example.ui.components.SupabaseDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.AiCounselorScreen
import com.example.ui.screens.DailyCheckInScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FacultyDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryContainerBlue

class MainActivity : ComponentActivity() {

    private val viewModel: CampusWellViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CampusWellApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CampusWellApp(viewModel: CampusWellViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val supabaseConfig by viewModel.supabaseConfig.collectAsState()
    val isTestingSupabase by viewModel.isTestingSupabase.collectAsState()
    val supabaseStatusMessage by viewModel.supabaseStatusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Gate on login status
    if (!userProfile.isLoggedIn) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { /* Automatically switches via userProfile.isLoggedIn */ }
                )
            }
        }
        return
    }

    val navItems = remember(userProfile.role) {
        Screen.getItemsForRole(userProfile.role)
    }

    var currentScreen by remember(userProfile.role) {
        mutableStateOf(navItems.first())
    }

    // Ensure current screen is valid for the current role
    LaunchedEffect(userProfile.role) {
        if (currentScreen !in navItems) {
            currentScreen = navItems.first()
        }
    }

    var showSupabaseDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Back button behavior
    val homeScreen = navItems.first()
    BackHandler(enabled = currentScreen != homeScreen) {
        currentScreen = homeScreen
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 768.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CampusWellHeader(
                    currentScreenTitle = currentScreen.title,
                    userProfile = userProfile,
                    isSupabaseConnected = supabaseConfig.isConnected,
                    onOpenSupabase = { showSupabaseDialog = true },
                    onOpenProfile = { showProfileDialog = true }
                )
            },
            bottomBar = {
                if (!isExpandedScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 2.dp
                    ) {
                        navItems.forEach { screen ->
                            val isSelected = currentScreen == screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = screen },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                modifier = Modifier.testTag("nav_${screen.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Side Navigation Rail for tablets/desktops
                if (isExpandedScreen) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = NavyDark,
                        contentColor = Color.White
                    ) {
                        navItems.forEach { screen ->
                            val isSelected = currentScreen == screen
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { currentScreen = screen },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text(screen.title, fontSize = 11.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8),
                                    indicatorColor = PrimaryContainerBlue
                                ),
                                modifier = Modifier.testTag("rail_nav_${screen.route}")
                            )
                        }
                    }
                }

                // Main Content Screen
                Box(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = currentScreen,
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.Dashboard -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { destination -> currentScreen = destination }
                            )
                            Screen.DailyCheckIn -> DailyCheckInScreen(
                                viewModel = viewModel
                            )
                            Screen.Progress -> ProgressScreen(
                                viewModel = viewModel
                            )
                            Screen.Support -> SupportScreen(
                                viewModel = viewModel
                            )
                            Screen.AiCounselor -> AiCounselorScreen(
                                viewModel = viewModel
                            )
                            Screen.FacultyDashboard -> FacultyDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToCheckIn = { currentScreen = Screen.DailyCheckIn }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showSupabaseDialog) {
        SupabaseDialog(
            config = supabaseConfig,
            isTesting = isTestingSupabase,
            statusMessage = supabaseStatusMessage,
            onTestConnection = { url, key -> viewModel.testSupabaseConnection(url, key) },
            onDismiss = { showSupabaseDialog = false }
        )
    }

    if (showProfileDialog) {
        ProfileDialog(
            userProfile = userProfile,
            onOpenSupabase = { showSupabaseDialog = true },
            onLogout = {
                viewModel.logout()
                showProfileDialog = false
            },
            onDismiss = { showProfileDialog = false }
        )
    }
}
