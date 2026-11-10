package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import java.util.Locale

@Composable
fun OnboardingScreenNew(onComplete: (UserSettings) -> Unit) {
    var glassCount by remember { mutableStateOf(8) }
    var glassSizeMl by remember { mutableStateOf(250) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderIntervalMinutes by remember { mutableStateOf(120) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB),
                        Color(0xFF90CAF9)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Titre principal
            Text(
                text = "Bienvenue",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    color = Color.White,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF1565C0),
                        offset = Offset(4f, 6f),
                        blurRadius = 12f
                    )
                )
            )

            Text(
                text = "Configuration simple",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    color = Color.White.copy(alpha = 0.95f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card objectif
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(text = "💧", style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp))

                    Text(
                        text = "Objectif quotidien",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    )

                    Text(
                        text = "${glassCount} verres (${String.format(Locale.getDefault(), "%.1f", glassCount * glassSizeMl / 1000f)} L)",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 40.sp,
                            color = Color(0xFF2196F3)
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { if (glassCount > 1) glassCount-- },
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("−", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        FilledTonalButton(
                            onClick = { if (glassCount < 25) glassCount++ },
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("+", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }
                    }

                    HorizontalDivider(thickness = 2.dp, color = Color(0xFFE3F2FD))

                    Text(
                        text = "Taille du verre : ${glassSizeMl} ml",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0))
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { if (glassSizeMl > 100) glassSizeMl -= 50 },
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF90CAF9))
                        ) {
                            Text("−", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        FilledTonalButton(
                            onClick = { if (glassSizeMl < 500) glassSizeMl += 50 },
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF90CAF9))
                        ) {
                            Text("+", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }
                    }
                }
            }

            // Card rappels
            Card(
                modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(36.dp))
                            Column {
                                Text("Rappels d'eau", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1565C0)))
                                Text("Notifications vocales", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575)))
                            }
                        }
                        Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                    }

                    if (reminderEnabled) {
                        Text("Fréquence :", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0)))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf(Pair(60, "Toutes les heures"), Pair(120, "Toutes les 2 heures"), Pair(180, "Toutes les 3 heures")).forEach { (minutes, label) ->
                                val isSelected = reminderIntervalMinutes == minutes
                                Card(
                                    onClick = { reminderIntervalMinutes = minutes },
                                    modifier = Modifier.fillMaxWidth().height(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF2196F3) else Color(0xFFE3F2FD)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(label, style = MaterialTheme.typography.titleLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 20.sp, color = if (isSelected) Color.White else Color(0xFF1565C0)))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { onComplete(UserSettings(dailyGoalGlasses = glassCount, glassSizeMl = glassSizeMl, userName = "Setti", reminderEnabled = reminderEnabled, reminderIntervalMinutes = reminderIntervalMinutes)) },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Commencer", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, fontSize = 32.sp, color = Color.White))
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
                }
            }
        }
    }
}
