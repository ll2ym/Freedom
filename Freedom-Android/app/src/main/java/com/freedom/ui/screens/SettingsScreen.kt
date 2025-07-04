package com.freedom.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freedom.data.model.User
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    currentUser: User,
    onLogout: () -> Unit,
    onResetKeys: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "Account",
                style = MaterialTheme.typography.titleMedium
            )

            SettingsItem(
                icon = Icons.Default.Person,
                label = "Username",
                value = currentUser.username,
                onClick = {}
            )

            SettingsItem(
                icon = Icons.Default.VpnKey,
                label = "Reset Encryption Keys",
                value = null,
                onClick = { onResetKeys() }
            )

            Divider()

            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium
            )

            SettingsItem(
                icon = Icons.Default.DarkMode,
                label = "Theme",
                value = "System Default",
                onClick = {
                    // Handle future theme changes
                }
            )

            Divider()

            SettingsItem(
                icon = Icons.Default.ExitToApp,
                label = "Log Out",
                value = null,
                onClick = { onLogout() }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
