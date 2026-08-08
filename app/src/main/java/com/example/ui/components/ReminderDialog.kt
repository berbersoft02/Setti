package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R

@Composable
fun ReminderDialog(
    userName: String = "Azul A Setti",
    onConfirmDrink: () -> Unit,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                .testTag("reminder_dialog_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "C'est l'heure !",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Black
                            )
                        )
                        Text(
                            text = "Bonjour, $userName !",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Pense à boire ton eau 💙",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Center Setti Illustration
                Image(
                    painter = painterResource(id = R.drawable.img_setti_avatar_1786213952641),
                    contentDescription = "Setti vous rappelle de boire",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Encouragement quote badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Ton corps te dira merci ❤️",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons Stack
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Primary Action: "J'ai bu mon eau !"
                    Button(
                        onClick = onConfirmDrink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .testTag("btn_confirm_drink"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "J'ai bu mon eau !",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            )
                        }
                    }

                    // Secondary Action: "Rappelle-moi plus tard"
                    OutlinedButton(
                        onClick = onSnooze,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            .testTag("btn_snooze_reminder"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Rappelle-moi plus tard",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
