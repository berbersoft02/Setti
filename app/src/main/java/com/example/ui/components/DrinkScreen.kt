package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.WaterLog
import java.util.Locale

@Composable
fun DrinkScreen(
    userSettings: UserSettings,
    todayLogs: List<WaterLog>,
    onDrinkWater: (Int) -> Unit,
    onUndoLast: (Int) -> Unit
) {
    val totalDrunkMl = todayLogs.sumOf { it.amountMl }
    val totalDrunkLiters = totalDrunkMl / 1000f
    val goalMl = userSettings.totalGoalMl
    val goalGlasses = userSettings.dailyGoalGlasses
    val glassSizeMl = userSettings.glassSizeMl

    val glassesDrunk = totalDrunkMl / glassSizeMl
    val remainingGlasses = (goalGlasses - glassesDrunk).coerceAtLeast(0)
    val progressFraction = (totalDrunkMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "waterProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Status Card (Bonjour ! Il vous manque encore X verres...)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .testTag("status_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Circular progress visual / avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_setti_avatar_1786213952641),
                        contentDescription = "Setti Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Bonjour !",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    if (remainingGlasses > 0) {
                        Text(
                            text = "Il vous manque encore $remainingGlasses verres pour atteindre votre objectif aujourd'hui.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 28.sp
                            )
                        )
                    } else {
                        Text(
                            text = "Objectif atteint ! Bravo pour votre excellente hydratation ! 🎉",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                lineHeight = 28.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. Main Progress Display (Consommation actuelle)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                .testTag("progress_display_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Consommation actuelle",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Liters counter display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f", totalDrunkLiters),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 72.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("liters_display_text")
                    )
                    Text(
                        text = "Litres",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Thick High-Contrast Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }

                // Progress Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Objectif : $goalGlasses Verres (${String.format(Locale.getDefault(), "%.1f", userSettings.totalGoalLiters)}L)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // 3. Primary Action Button: "Boire un Verre (+250ml)"
        Button(
            onClick = { onDrinkWater(glassSizeMl) },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .testTag("btn_boire_un_verre"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Boire un Verre",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = "+ ${glassSizeMl}ml",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        // 4. Quick Add Presets (+100ml, +250ml, +500ml)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Autres formats rapides :",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Pair(100, "Petit verre\n100ml"),
                    Pair(250, "Verre std\n250ml"),
                    Pair(500, "Gourde\n500ml")
                ).forEach { (amount, label) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable { onDrinkWater(amount) }
                            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            .testTag("preset_$amount"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }

        // Undo option if logs exist
        if (todayLogs.isNotEmpty()) {
            OutlinedButton(
                onClick = { onUndoLast(todayLogs.first().id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    .testTag("btn_undo_last"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Annuler la dernière prise (${todayLogs.first().amountMl}ml)",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
