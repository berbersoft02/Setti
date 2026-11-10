package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int,
    max: Int,
    step: Int = 1,
    formatValue: (Int) -> String = { it.toString() }
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onValueChange((value - step).coerceAtLeast(min)) },
            modifier = Modifier
                .weight(1f)
                .height(110.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            enabled = value > min
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Diminuer",
                modifier = Modifier.size(48.dp)
            )
        }

        Card(
            modifier = Modifier
                .weight(1.8f)
                .height(130.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 64.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = formatValue(value),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = { onValueChange((value + step).coerceAtMost(max)) },
            modifier = Modifier
                .weight(1f)
                .height(110.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            enabled = value < max
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Augmenter",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
