package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DrinkScreen
import com.example.ui.components.HistoryScreen
import com.example.ui.components.ReminderDialog
import com.example.ui.components.SettiBottomNavBar
import com.example.ui.components.SettiHeader
import com.example.ui.components.SettingsScreen
import com.example.ui.theme.SettiFlowTheme
import com.example.viewmodel.WaterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettiFlowTheme {
                SettiFlowApp()
            }
        }
    }
}

@Composable
fun SettiFlowApp(viewModel: WaterViewModel = viewModel()) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
    val allLogs by viewModel.allLogs.collectAsStateWithLifecycle()
    val showReminderDialog by viewModel.showReminderDialog.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SettiHeader(
                activeTab = activeTab,
                onTabSelected = { viewModel.setActiveTab(it) },
                onOpenReminder = { viewModel.openReminderDialog() }
            )
        },
        bottomBar = {
            SettiBottomNavBar(
                selectedTab = activeTab,
                onTabSelected = { viewModel.setActiveTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> DrinkScreen(
                    userSettings = userSettings,
                    todayLogs = todayLogs,
                    onDrinkWater = { amount -> viewModel.drinkWater(amount) },
                    onUndoLast = { id -> viewModel.removeLog(id) }
                )
                1 -> HistoryScreen(
                    userSettings = userSettings,
                    allLogs = allLogs,
                    onDeleteLog = { id -> viewModel.removeLog(id) },
                    onAddCustomLog = { amount -> viewModel.drinkWater(amount) }
                )
                2 -> SettingsScreen(
                    userSettings = userSettings,
                    onSaveSettings = { settings ->
                        viewModel.updateSettings(settings)
                        viewModel.setActiveTab(0)
                    }
                )
            }
        }
    }

    if (showReminderDialog) {
        ReminderDialog(
            userName = userSettings.userName,
            onConfirmDrink = { viewModel.confirmReminderDrink() },
            onSnooze = { viewModel.snoozeReminder() },
            onDismiss = { viewModel.closeReminderDialog() }
        )
    }
}
