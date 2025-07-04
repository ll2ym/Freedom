package com.freedom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatBubble(
    message: String,
    isOwnMessage: Boolean,
    sender: String? = null,
    timestamp: String? = null
) {
    val bubbleColor = if (isOwnMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (isOwnMessage) Color.White else Color.Black
    val alignment = if (isOwnMessage) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        if (!isOwnMessage && sender != null) {
            Text(sender, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(message, color = textColor, style = MaterialTheme.typography.bodyLarge)
        }
        if (timestamp != null) {
            Text(timestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
