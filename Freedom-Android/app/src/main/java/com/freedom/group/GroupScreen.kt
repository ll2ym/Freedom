package com.freedom.group

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GroupScreen(groups: List<String>, onCreateGroup: (String) -> Unit) {
    var groupName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Groups", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        groups.forEach {
            Text(it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("New Group") }
        )
        Button(onClick = { onCreateGroup(groupName); groupName = "" }) {
            Text("Create Group")
        }
    }
}
