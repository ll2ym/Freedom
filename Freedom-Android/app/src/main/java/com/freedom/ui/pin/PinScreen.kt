package com.freedom.ui.pin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PinScreen(
    viewModel: PinViewModel = hiltViewModel(),
    onPinSuccess: () -> Unit
) {
    val pin = viewModel.pin.value
    val keypadNumbers = viewModel.keypadNumbers.value

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter PIN")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "*".repeat(pin.length))
        Spacer(modifier = Modifier.height(32.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.width(240.dp)
        ) {
            items(keypadNumbers) { number ->
                Button(
                    onClick = { viewModel.onKeyPress(number) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = number.toString())
                }
            }
            item {
                Button(
                    onClick = { viewModel.onBackspace() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "<-")
                }
            }
        }
    }

    if (viewModel.isPinCorrect.value) {
        onPinSuccess()
    }
}
