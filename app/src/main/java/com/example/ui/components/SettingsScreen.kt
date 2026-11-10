package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserSettings
import java.util.Locale

@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    onSaveSettings: (UserSettings) -> Unit
) {
    var glassCount by remember(userSettings) { mutableStateOf(userSettings.dailyGoalGlasses) }
    var glassSizeMl by remember(userSettings) { mutableStateOf(userSettings.glassSizeMl) }
    var userName by remember(userSettings) { mutableStateOf(userSettings.userName) }
    var reminderEnabled by remember(userSettings) { mutableStateOf(userSettings.reminderEnabled) }
    var reminderIntervalMinutes by remember(userSettings) { mutableStateOf(userSettings.reminderIntervalMinutes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Goal Illustration Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .testTag("settings_hero_card"),
            shape = RoundedCornerShape(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_set_goal_hero_1786213964548),
                contentDescription = "Set Water Goal Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Core Question Title
        Text(
            text = "Quelle quantité d'eau voulez-vous boire chaque jour ?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                lineHeight = 38.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Advice Box Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "La plupart des médecins suggèrent de commencer avec 8 verres (2.0L). Vous pourrez changer cela plus tard.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(20.dp)
            )
        }

        // Goal Stepper (reusable component)
        Stepper(
            value = glassCount,
            onValueChange = { glassCount = it },
            min = 1,
            max = 25,
            formatValue = { glasses ->
                "Verres (${String.format(Locale.getDefault(), "%.1f", glasses * glassSizeMl / 1000f)}L)"
            }
        )

        // Glass Size Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Taille de votre verre",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Stepper(
                value = glassSizeMl,
                onValueChange = { glassSizeMl = it },
                min = 100,
                max = 500,
                step = 50,
                formatValue = { "$it ml" }
            )
        }

        // Additional Customization Options
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Input
                Text(
                    text = "Votre Prénom / Salutation",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_user_name"),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                // Reminder Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Rappels d'eau",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Recevoir des notifications",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        modifier = Modifier.testTag("switch_reminder_enabled")
                    )
                }

                if (reminderEnabled) {
                    Text(
                        text = "Fréquence des rappels :",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair(60, "Toutes les 1h"),
                            Pair(120, "Toutes les 2h"),
                            Pair(180, "Toutes les 3h")
                        ).forEach { (minutes, label) ->
                            val isSelected = reminderIntervalMinutes == minutes
                            FilterChip(
                                selected = isSelected,
                                onClick = { reminderIntervalMinutes = minutes },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Primary Action: "Enregistrer et Commencer"
        Button(
            onClick = {
                val updated = userSettings.copy(
                    dailyGoalGlasses = glassCount,
                    glassSizeMl = glassSizeMl,
                    userName = userName,
                    reminderEnabled = reminderEnabled,
                    reminderIntervalMinutes = reminderIntervalMinutes
                )
                onSaveSettings(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .testTag("btn_save_settings"),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enregistrer et Commencer",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                )

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
