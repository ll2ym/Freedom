package com.freedom.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Settings
        OutlinedTextField(
            value = state.displayName,
            onValueChange = { viewModel.onDisplayNameChange(it) },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.status,
            onValueChange = { viewModel.onStatusChange(it) },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Notification Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show notifications when locked")
            Switch(
                checked = state.showNotificationsWhenLocked,
                onCheckedChange = { viewModel.onShowNotificationsChange(it) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Data Wipe Settings
        Text("Data Wipe", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.wipeAfterLogout,
            onValueChange = { viewModel.onWipeAfterLogoutChange(it) },
            label = { Text("Wipe after logout (dd:hh:mm)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.wipeAfterNoConnection,
            onValueChange = { viewModel.onWipeAfterNoConnectionChange(it) },
            label = { Text("Wipe after no connection (dd:hh:mm)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Duress PIN Settings
        Text("Duress PIN", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.duressPin,
            onValueChange = { viewModel.onDuressPinChange(it) },
            label = { Text("Duress PIN") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.duressMessage,
            onValueChange = { viewModel.onDuressMessageChange(it) },
            label = { Text("Duress Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveSettings() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}
