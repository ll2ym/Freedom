package com.freedom.auth

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InviteOnlyGate(allowed: Boolean, content: @Composable () -> Unit) {
    if (allowed) {
        content()
    } else {
        Text("Access denied. Invite only.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
    }
}
