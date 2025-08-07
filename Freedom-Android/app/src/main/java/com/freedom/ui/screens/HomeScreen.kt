package com.freedom.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.freedom.data.model.User
import com.freedom.ui.components.UserAvatar

@Composable
fun HomeScreen(
    navController: NavController,
    currentUser: User,
    recentChats: List<User>,
    onNewChatClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Freedom") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewChatClick) {
                Icon(Icons.Default.Chat, contentDescription = "New Chat")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ) {
            Text(
                text = "Welcome, ${currentUser.username}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (recentChats.isEmpty()) {
                Text("No recent chats. Start a secure conversation.")
            } else {
                LazyColumn {
                    items(recentChats) { user ->
                        ChatListItem(user) {
                            navController.navigate("chat/${user.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        UserAvatar(username = user.username, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.username, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Tap to open chat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
