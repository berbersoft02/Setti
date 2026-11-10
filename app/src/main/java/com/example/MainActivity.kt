package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DrinkScreen
import com.example.ui.components.DrinkScreeniOS
import com.example.ui.components.HistoryScreen
import com.example.ui.components.OnboardingScreenNew
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

    // Request notification permission once on launch (Android 13+).
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission state is checked again before posting notifications. */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (!userSettings.onboardingCompleted) {
        OnboardingScreenNew(onComplete = { settings -> viewModel.finishOnboarding(settings) })
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DrinkScreeniOS(
                userSettings = userSettings,
                todayLogs = todayLogs,
                onDrinkWater = { amount -> viewModel.drinkWater(amount) },
                onUndoLast = { id -> viewModel.removeLog(id) }
            )
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
