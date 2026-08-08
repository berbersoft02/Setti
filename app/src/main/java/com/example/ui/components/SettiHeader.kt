package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettiHeader(
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    onOpenReminder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Logo & Setti Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_setti_avatar_1786213952641),
                        contentDescription = "Azza Setti Avatar",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "SettiFlow",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("app_title")
                    )
                }

                // Right: Reminder Bell Button
                IconButton(
                    onClick = onOpenReminder,
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .testTag("reminder_bell_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Rappel d'eau",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // High-contrast bottom divider line
            HorizontalDivider(
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
