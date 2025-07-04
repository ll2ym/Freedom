package com.freedom.call

import android.Manifest
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import org.webrtc.SurfaceViewRenderer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun CallScreen(
    viewModel: CallViewModel,
    webRTCClient: WebRTCClient,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val callState by viewModel.callState.collectAsState()
    val permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    // Request permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            val allGranted = granted.values.all { it }
            if (allGranted) {
                webRTCClient.initPeerConnection(listOf()) // Pass ICE servers if needed
            } else {
                viewModel.reportError("Permissions denied")
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                SurfaceViewRenderer(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    init(webRTCClient.getEglBaseContext(), null)
                    setZOrderMediaOverlay(true)
                    webRTCClient.localVideoTrack?.addSink(this)
                    ViewCompat.setTranslationZ(this, 10f)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.endCall()
                    onEndCall()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("End Call", color = Color.White)
            }
        }

        if (callState is CallState.Error) {
            Text(
                text = (callState as CallState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
