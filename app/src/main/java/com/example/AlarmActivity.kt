package com.example

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.ui.theme.SettiFlowTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class AlarmActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private var originalVolume: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize audio components
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        originalVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

        // Set volume to maximum
        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

        // Initialize vibrator
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Initialize TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.FRENCH
                lifecycleScope.launch {
                    delay(500)
                    speakReminder()
                }
            }
        }

        // Start alarm sound
        startAlarmSound()

        // Start vibration pattern
        startVibration()

        setContent {
            SettiFlowTheme {
                AlarmScreen(
                    onDismiss = { finishAlarm() }
                )
            }
        }

        // Auto-dismiss after 3 minutes OR repeat if not dismissed
        lifecycleScope.launch {
            repeat(3) { repetition ->
                delay(180_000) // 3 minutes
                if (!isFinishing) {
                    // Restart alarm sounds and voice
                    ringtone?.play()
                    startVibration()
                    speakReminder()
                }
            }
            // Final auto-dismiss after 3 repetitions (9 minutes total)
            if (!isFinishing) {
                finishAlarm()
            }
        }
    }

    private fun startAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(this, alarmUri)
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        try {
            // Long vibration pattern: vibrate for 1 second, pause 0.5s, repeat
            val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000, 500)
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun speakReminder() {
        val userName = "Setti"
        val message = "$userName, c'est l'heure de boire ton eau ! Bois un petit verre d'eau s'il te plaît."
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "WATER_REMINDER")

        // Repeat the message 3 times with delays
        lifecycleScope.launch {
            repeat(2) {
                delay(5000)
                tts?.speak(message, TextToSpeech.QUEUE_ADD, null, "WATER_REMINDER_$it")
            }
        }
    }

    private fun finishAlarm() {
        // Stop all alarms
        ringtone?.stop()
        vibrator?.cancel()
        tts?.stop()

        // Restore original volume
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
        ringtone?.stop()
        vibrator?.cancel()
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
    }
}

@Composable
fun AlarmScreen(onDismiss: () -> Unit) {
    // Pulsing animation for the water drop
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Gradient animé
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF3B82F6),
                        Color(0xFF60A5FA),
                        Color(0xFF93C5FD)
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        // Bulles animées en arrière-plan
        repeat(6) { index ->
            val bubbleOffset by infiniteTransition.animateFloat(
                initialValue = 1000f,
                targetValue = -200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + index * 600,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "bubble$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (60 + index * 60).dp,
                        y = bubbleOffset.dp
                    )
                    .size((30 + index * 10).dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Message vocal visible en gros
            Text(
                text = "Setti,\nc'est l'heure\nde boire !",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 48.sp,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 56.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(4f, 8f),
                        blurRadius = 16f
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Giant animated water drop icon with 3D effect
            Box(
                modifier = Modifier
                    .size(300.dp * scale)
                    .shadow(
                        elevation = 40.dp,
                        shape = CircleShape,
                        spotColor = Color.Cyan.copy(alpha = 0.6f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Boire de l'eau",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texte d'instruction simple
            Text(
                text = "Touche n'importe où\npour confirmer",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.W600,
                    fontSize = 28.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                        blurRadius = 8f
                    )
                )
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
