package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.UserSettings
import com.example.data.WaterLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    userSettings: UserSettings,
    allLogs: List<WaterLog>,
    onDeleteLog: (Int) -> Unit,
    onAddCustomLog: (Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("250") }

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayLogs = allLogs.filter { it.dateString == todayStr }
    val totalTodayMl = todayLogs.sumOf { it.amountMl }
    val targetMl = userSettings.totalGoalMl
    val isGoalAchieved = totalTodayMl >= targetMl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Daily Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .testTag("history_summary_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Aujourd'hui",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    if (isGoalAchieved) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Objectif atteint !",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total bu",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "${totalTodayMl} ml / ${targetMl} ml",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("btn_add_manual_log")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ajouter", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        Text(
            text = "Journal des verres bu",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Water Logs List
        if (allLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Aucun verre d'eau enregistré pour le moment.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allLogs, key = { it.id }) { log ->
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val timeStr = timeFormat.format(Date(log.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            .testTag("log_item_${log.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "+${log.amountMl} ml",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        text = "${log.dateString} à $timeStr",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteLog(log.id) },
                                modifier = Modifier.testTag("delete_log_${log.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer l'entrée",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Manual Water Log Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Ajouter de l'eau manuellement",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Entrez la quantité en millilitres (ml) :",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { customAmountText = it.filter { char -> char.isDigit() } },
                        label = { Text("Quantité (ml)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customAmountText.toIntOrNull() ?: 250
                        onAddCustomLog(amount)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
