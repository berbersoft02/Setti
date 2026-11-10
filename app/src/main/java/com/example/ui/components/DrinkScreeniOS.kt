package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserSettings
import com.example.data.WaterLog
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.sin

@Composable
fun DrinkScreeniOS(
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
    val glassesDrunk = (totalDrunkMl / glassSizeMl).coerceAtMost(userSettings.dailyGoalGlasses)
    val glassesRemaining = (userSettings.dailyGoalGlasses - glassesDrunk).coerceAtLeast(0)

    // Animation fluide du remplissage de l'eau
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "waterFill"
    )

    // TTS pour feedback vocal
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.FRENCH
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
            tts = null
        }
    }

    // État du ripple au toucher
    var rippleTrigger by remember { mutableIntStateOf(0) }
    var ripplePosition by remember { mutableStateOf(Offset.Zero) }
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    LaunchedEffect(rippleTrigger) {
        if (rippleTrigger > 0) {
            rippleRadius.snapTo(0f)
            rippleAlpha.snapTo(0.6f)
            launch { rippleRadius.animateTo(500f, tween(900, easing = FastOutSlowInEasing)) }
            launch { rippleAlpha.animateTo(0f, tween(900)) }
        }
    }

    // Message motivant selon le progrès
    val motivationalMessage = when {
        progressFraction >= 1f -> "🎉 Objectif atteint ! Bravo !"
        progressFraction >= 0.75f -> "💪 Presque là !"
        progressFraction >= 0.5f -> "✨ Continue comme ça !"
        progressFraction >= 0.25f -> "👍 Bon début !"
        else -> "💧 C'est parti !"
    }

    // Heure du dernier verre
    val lastDrinkTime = if (todayLogs.isNotEmpty()) {
        val lastLog = todayLogs.first()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = lastLog.timestamp
        }
        String.format(Locale.getDefault(), "%02d:%02d",
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE))
    } else {
        "--:--"
    }

    // Calcul du temps avant la prochaine alarme
    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val isQuietHours = currentHour >= userSettings.quietHoursStart || currentHour < userSettings.quietHoursEnd

    val nextAlarmText = if (isQuietHours) {
        // En période silencieuse - prochaine alarme à la fin des quiet hours
        val hoursUntilEnd = if (currentHour >= userSettings.quietHoursStart) {
            (24 - currentHour) + userSettings.quietHoursEnd
        } else {
            userSettings.quietHoursEnd - currentHour
        }
        if (hoursUntilEnd > 1) {
            "🔕 Silence (dans ${hoursUntilEnd}h)"
        } else {
            "🔕 Silence (bientôt)"
        }
    } else {
        // Calcul basé sur l'intervalle de rappel
        val intervalMinutes = userSettings.reminderIntervalMinutes
        val hoursInterval = intervalMinutes / 60
        val minutesRemainder = intervalMinutes % 60

        if (hoursInterval > 0 && minutesRemainder > 0) {
            "⏰ Prochain rappel dans ${hoursInterval}h${minutesRemainder}min"
        } else if (hoursInterval > 0) {
            "⏰ Prochain rappel dans ${hoursInterval}h"
        } else {
            "⏰ Prochain rappel dans ${minutesRemainder}min"
        }
    }

    // Animation pulsante pour le bouton
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Gradient adaptatif selon l'heure (bleu jour, violet soir, orange matin)
    val gradientColors = when (currentHour) {
        in 6..11 -> listOf(  // Matin - Orange/Rose
            Color(0xFFFF6B6B),
            Color(0xFFFF8E53),
            Color(0xFFFFA94D),
            Color(0xFFFFD93D)
        )
        in 12..17 -> listOf(  // Après-midi - Bleu ciel
            Color(0xFF1E3A8A),
            Color(0xFF3B82F6),
            Color(0xFF60A5FA),
            Color(0xFF93C5FD)
        )
        in 18..21 -> listOf(  // Soirée - Violet/Rose
            Color(0xFF6B21A8),
            Color(0xFF9333EA),
            Color(0xFFA855F7),
            Color(0xFFC084FC)
        )
        else -> listOf(  // Nuit - Bleu profond
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF475569)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = gradientColors)
            )
    ) {
        // Bulles d'eau flottantes en arrière-plan
        repeat(8) { index ->
            val bubbleOffset by infiniteTransition.animateFloat(
                initialValue = 1000f,
                targetValue = -200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 5000 + index * 800,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "bubble$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (50 + index * 45).dp,
                        y = bubbleOffset.dp
                    )
                    .size((20 + index * 8).dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            )
        }

        // Étoiles scintillantes en arrière-plan
        repeat(12) { index ->
            val sparkleAlpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1200 + index * 300,
                        easing = EaseInOut
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sparkle$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = ((index * 97) % 380).dp,
                        y = ((index * 173) % 700).dp
                    )
                    .size((3 + index % 4).dp)
                    .background(Color.White.copy(alpha = sparkleAlpha * 0.7f), CircleShape)
            )
        }

        // Halo lumineux derrière le verre
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(500.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo de l'app - PLUS GROS
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Logo Setti",
                modifier = Modifier
                    .size(180.dp)  // 120 → 180
                    .shadow(
                        elevation = 24.dp,  // 16 → 24
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.4f),
                        spotColor = Color.White.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
            )

            // Message motivant
            Text(
                text = motivationalMessage,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.W700,
                    fontSize = 26.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                        blurRadius = 8f
                    )
                )
            )

            // Sous-titre avec objectif
            Text(
                text = "${String.format(Locale.getDefault(), "%.1f", totalDrunkLiters)} L / ${String.format(Locale.getDefault(), "%.1f", userSettings.totalGoalLiters)} L",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                        blurRadius = 8f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // VERRE D'EAU 3D INTERACTIF - Cliquable avec ripple + voix
            Box(
                modifier = Modifier
                    .size(380.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            ripplePosition = tapOffset
                            rippleTrigger++
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tts?.speak(
                                "Bravo Setti ! Tu as bu ton eau !",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "drink_feedback"
                            )
                            onDrinkWater(glassSizeMl)
                        }
                    }
                    .testTag("drink_button"),
                contentAlignment = Alignment.Center
            ) {
                // Animation de vague pour l'eau
                val waveOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "wave"
                )

                // Lueur néon autour du verre
                Box(
                    modifier = Modifier
                        .size(340.dp, 420.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF22D3EE).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(320.dp, 400.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(
                                topStart = 40.dp,
                                topEnd = 40.dp,
                                bottomStart = 70.dp,
                                bottomEnd = 70.dp
                            ),
                            ambientColor = Color.Cyan.copy(alpha = 0.4f),
                            spotColor = Color.White.copy(alpha = 0.6f)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 40.dp,
                                topEnd = 40.dp,
                                bottomStart = 70.dp,
                                bottomEnd = 70.dp
                            )
                        )
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.20f),
                                    Color.White.copy(alpha = 0.08f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Niveau d'eau qui monte avec gradient brillant - ANIMÉ
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedProgress.coerceIn(0.05f, 1f))
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF22D3EE).copy(alpha = 0.90f),
                                        Color(0xFF0EA5E9).copy(alpha = 0.95f),
                                        Color(0xFF2563EB)
                                    )
                                )
                            )
                    ) {
                        // Vague ondulante animée sur le dessus
                        val waveShift by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "waveShift"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (-12).dp)
                                .drawBehind {
                                    val wavePath = Path()
                                    val w = size.width
                                    val h = size.height
                                    wavePath.moveTo(0f, h * 0.5f)
                                    var x = 0f
                                    while (x <= w) {
                                        val y = h * 0.5f + sin((x / w * 3 + waveShift * 3) * Math.PI).toFloat() * h * 0.35f
                                        wavePath.lineTo(x, y)
                                        x += 8f
                                    }
                                    wavePath.lineTo(w, h)
                                    wavePath.lineTo(0f, h)
                                    wavePath.close()
                                    drawPath(
                                        path = wavePath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF67E8F9),
                                                Color(0xFF22D3EE).copy(alpha = 0.9f)
                                            )
                                        )
                                    )
                                }
                        )

                        // Bulles animées DANS l'eau
                        if (animatedProgress > 0.1f) {
                            repeat(5) { index ->
                                val innerBubble by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 2500 + index * 700,
                                            easing = LinearEasing
                                        ),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "innerBubble$index"
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .offset(
                                            x = (40 + index * 55).dp,
                                            y = (-innerBubble * 250).dp
                                        )
                                        .size((8 + index * 3).dp)
                                        .background(
                                            Color.White.copy(alpha = 0.35f),
                                            CircleShape
                                        )
                                )
                            }
                        }

                        // Reflet brillant sur l'eau
                        Box(
                            modifier = Modifier
                                .size(60.dp, 120.dp)
                                .align(Alignment.CenterEnd)
                                .offset(x = (-40).dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                        )

                        // Ligne de lumière en haut de l'eau
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.8f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Contour du verre - bords 3D
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Bord gauche (ombre)
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight()
                                .align(Alignment.CenterStart)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Bord droit (reflet)
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                    }

                    // Chiffre des litres au centre
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", totalDrunkLiters),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.W900,
                                fontSize = 110.sp,
                                color = Color.White,
                                letterSpacing = (-3).sp,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    offset = androidx.compose.ui.geometry.Offset(5f, 10f),
                                    blurRadius = 20f
                                )
                            )
                        )
                        Text(
                            text = "litres",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.W700,
                                fontSize = 32.sp,
                                color = Color.White.copy(alpha = 0.95f),
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offset = androidx.compose.ui.geometry.Offset(3f, 6f),
                                    blurRadius = 12f
                                )
                            )
                        )
                    }
                }

                // Ripple splash au point de toucher
                Canvas(modifier = Modifier.matchParentSize()) {
                    if (rippleAlpha.value > 0f) {
                        drawCircle(
                            color = Color.White,
                            radius = rippleRadius.value,
                            center = ripplePosition,
                            alpha = rippleAlpha.value
                        )
                        drawCircle(
                            color = Color(0xFF67E8F9),
                            radius = rippleRadius.value * 0.6f,
                            center = ripplePosition,
                            alpha = rippleAlpha.value * 0.7f
                        )
                    }
                }

                // Indication tactile qui pulse
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 60.dp)
                        .scale(pulseScale)
                ) {
                    Text(
                        text = "👆 Touche le verre",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W800,
                            fontSize = 28.sp,
                            color = Color.White.copy(alpha = 0.95f),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = androidx.compose.ui.geometry.Offset(3f, 6f),
                                blurRadius = 10f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Infos supplémentaires
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Temps avant la prochaine alarme
                Text(
                    text = nextAlarmText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.W700,
                        fontSize = 24.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                            blurRadius = 8f
                        )
                    )
                )

                if (glassesRemaining > 0) {
                    Text(
                        text = "Encore $glassesRemaining verre${if (glassesRemaining > 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W600,
                            fontSize = 22.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = androidx.compose.ui.geometry.Offset(2f, 3f),
                                blurRadius = 6f
                            )
                        )
                    )
                }

                if (todayLogs.isNotEmpty()) {
                    Text(
                        text = "Dernier verre : $lastDrinkTime",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.W500,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }

            // Compteur de verres avec effet glassmorphism
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(userSettings.dailyGoalGlasses.coerceAtMost(8)) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(18.dp)  // 16 → 18
                                .shadow(
                                    elevation = if (index < glassesDrunk) 6.dp else 2.dp,
                                    shape = CircleShape
                                )
                                .background(
                                    color = if (index < glassesDrunk) Color(0xFFFCD34D) else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
