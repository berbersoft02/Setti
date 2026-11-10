package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.data.WaterLog
import java.util.Locale
import kotlin.math.sin

@Composable
fun DrinkScreen(
    userSettings: UserSettings,
    todayLogs: List<WaterLog>,
    onDrinkWater: (Int) -> Unit,
    onUndoLast: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val totalDrunkMl = todayLogs.sumOf { it.amountMl }
    val totalDrunkLiters = totalDrunkMl / 1000f
    val goalMl = userSettings.totalGoalMl
    val glassSizeMl = userSettings.glassSizeMl

    val progressFraction = (totalDrunkMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 1200, easing = EaseInOutCubic),
        label = "waterProgress"
    )

    val glassesDrunk = (totalDrunkMl / glassSizeMl).coerceAtMost(userSettings.dailyGoalGlasses)

    // Animation pour les bulles
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")
    val bubbleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleOffset"
    )

    // Animation du liquide (vagues)
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre élégant
            Text(
                text = "Setti",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 64.sp,
                    color = Color.White,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF1565C0),
                        offset = Offset(4f, 6f),
                        blurRadius = 12f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Message d'encouragement
            Text(
                text = if (progressFraction >= 1f) "🎉 Objectif atteint !"
                      else if (progressFraction >= 0.7f) "Continue comme ça ! 💪"
                      else if (progressFraction >= 0.4f) "Bon courage ! 💧"
                      else "N'oublie pas de boire ! 💙",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    color = Color.White.copy(alpha = 0.95f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Compteur visuel moderne avec cartes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(userSettings.dailyGoalGlasses.coerceAtMost(10)) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index < glassesDrunk) 52.dp else 48.dp)
                            .shadow(
                                elevation = if (index < glassesDrunk) 8.dp else 2.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                if (index < glassesDrunk)
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF42A5F5),
                                            Color(0xFF1E88E5)
                                        )
                                    )
                                else
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.9f),
                                            Color.White.copy(alpha = 0.7f)
                                        )
                                    ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = if (index < glassesDrunk) Color.White else Color(0xFFBBDEFB),
                            modifier = Modifier.size(if (index < glassesDrunk) 28.dp else 24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verre d'eau 3D réaliste avec animations
            Box(
                modifier = Modifier
                    .size(300.dp, 420.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ombre du verre
                Box(
                    modifier = Modifier
                        .size(280.dp, 400.dp)
                        .offset(y = 10.dp)
                        .blur(20.dp)
                        .background(
                            Color.Black.copy(alpha = 0.15f),
                            RoundedCornerShape(32.dp)
                        )
                )

                // Verre principal avec effet glassmorphism
                Box(
                    modifier = Modifier
                        .size(280.dp, 400.dp)
                        .shadow(24.dp, RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(32.dp)
                        )
                ) {
                    // Eau avec animation de vagues
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        val waterHeight = size.height * animatedProgress
                        val waterTop = size.height - waterHeight

                        // Dessiner l'eau avec effet de vagues
                        val path = Path().apply {
                            moveTo(0f, size.height)
                            lineTo(0f, waterTop)

                            // Créer des vagues
                            val waveWidth = size.width / 4
                            var x = 0f
                            while (x < size.width) {
                                val y = waterTop + sin((x / waveWidth + waveOffset) * 2) * 8f
                                lineTo(x, y)
                                x += 5f
                            }

                            lineTo(size.width, waterTop)
                            lineTo(size.width, size.height)
                            close()
                        }

                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2196F3),
                                    Color(0xFF1976D2),
                                    Color(0xFF1565C0)
                                ),
                                startY = waterTop,
                                endY = size.height
                            ),
                            style = Fill
                        )

                        // Bulles animées
                        if (animatedProgress > 0.1f) {
                            repeat(5) { index ->
                                val bubbleX = size.width * (index + 1) / 6f
                                val bubbleY = waterTop + (bubbleOffset % 100) + (index * 20f)
                                if (bubbleY < size.height && bubbleY > waterTop) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.5f),
                                        radius = (4 + index).dp.toPx(),
                                        center = Offset(bubbleX, bubbleY)
                                    )
                                }
                            }
                        }
                    }

                    // Reflet du verre
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(size.width * 0.3f, size.height * 0.3f)
                            )
                        )
                    }

                    // Texte des litres par-dessus
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f L", totalDrunkLiters),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 72.sp,
                                color = if (progressFraction > 0.5f) Color.White else Color(0xFF1565C0),
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = Offset(4f, 4f),
                                    blurRadius = 8f
                                )
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton moderne avec effet 3D
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                label = "buttonScale"
            )

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDrinkWater(glassSizeMl)
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(130.dp)
                    .scale(scale)
                    .shadow(
                        elevation = if (isPressed) 8.dp else 20.dp,
                        shape = RoundedCornerShape(40.dp),
                        spotColor = Color(0xFF1976D2).copy(alpha = 0.5f)
                    )
                    .testTag("btn_boire_un_verre"),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 8.dp
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF42A5F5),
                                    Color(0xFF2196F3),
                                    Color(0xFF1976D2)
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🥤",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp)
                        )
                        Text(
                            text = "J'AI BU",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 56.sp,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = Offset(2f, 4f),
                                    blurRadius = 8f
                                )
                            )
                        )
                        Text(
                            text = "💧",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp)
                        )
                    }
                }
            }
        }
    }
}
