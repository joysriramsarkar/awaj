package com.awaj.assistant

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awaj.assistant.ui.MainViewModel
import com.awaj.assistant.ui.history.HistoryScreen
import com.awaj.assistant.ui.home.HomeScreen
import com.awaj.assistant.ui.permissions.PermissionsScreen
import com.awaj.assistant.ui.routines.RoutinesScreen
import com.awaj.assistant.ui.settings.SettingsScreen
import com.awaj.assistant.ui.theme.AwajTheme
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.DarkBackground
import com.awaj.assistant.ui.theme.DarkSurface
import com.awaj.assistant.ui.theme.TextMuted

enum class NavigationItem(val titleBangla: String, val icon: ImageVector) {
    HOME("সহকারী", Icons.Filled.Mic),
    ROUTINES("রুটিন", Icons.Filled.Schedule),
    HISTORY("ইতিহাস", Icons.Filled.History),
    PERMISSIONS("পারমিশন", Icons.Filled.Security),
    SETTINGS("সেটিংস", Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Audio permission granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLockScreenVisibility()

        // Request basic audio permission on first start
        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        handleIntentTrigger(intent)

        setContent {
            AwajTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setupLockScreenVisibility()
        handleIntentTrigger(intent)
    }

    private fun setupLockScreenVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    private fun handleIntentTrigger(intent: Intent?) {
        if (intent?.getBooleanExtra("action_trigger_mic", false) == true) {
            viewModel.startListeningForWakeWord()
        }
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationItem.values().forEach { item ->
                    val isSelected = selectedItem == item
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleBangla,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.titleBangla,
                                fontSize = 11.sp,
                                color = if (isSelected) BrandPrimary else TextMuted
                            )
                        },
                        selected = isSelected,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            unselectedIconColor = TextMuted,
                            indicatorColor = BrandPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                NavigationItem.HOME -> HomeScreen(viewModel = viewModel)
                NavigationItem.ROUTINES -> RoutinesScreen(viewModel = viewModel)
                NavigationItem.HISTORY -> HistoryScreen(viewModel = viewModel)
                NavigationItem.PERMISSIONS -> PermissionsScreen()
                NavigationItem.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
