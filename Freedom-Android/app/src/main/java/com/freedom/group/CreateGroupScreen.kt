package com.freedom.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(
    currentUser: String,
    onCreateGroup: (Group) -> Unit,
    onBack: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOf(currentUser)) }
    var newMember by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Create New Group", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Members", style = MaterialTheme.typography.titleMedium)

        members.forEach { member ->
            Text(text = "• $member", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newMember,
            onValueChange = { newMember = it },
            label = { Text("Add Member by Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (newMember.isNotBlank()) {
                    members = members + newMember.trim()
                    newMember = ""
                    focusManager.clearFocus()
                }
            }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onCreateGroup(
                            Group(
                                id = groupName.trim().lowercase(),
                                name = groupName.trim(),
                                members = members
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Create Group")
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}
