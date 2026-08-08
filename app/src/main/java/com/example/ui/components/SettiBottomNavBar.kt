package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettiBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column {
            HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTabItem(
                    title = "Boire",
                    icon = Icons.Default.WaterDrop,
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    testTag = "tab_boire"
                )

                NavTabItem(
                    title = "Historique",
                    icon = Icons.Default.CalendarMonth,
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    testTag = "tab_historique"
                )

                NavTabItem(
                    title = "Réglages",
                    icon = Icons.Default.Settings,
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    testTag = "tab_reglages"
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(backgroundColor)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = contentColor
                )
            )
        }
    }
}
