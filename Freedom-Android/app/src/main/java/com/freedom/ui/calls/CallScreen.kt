package com.freedom.ui.calls

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.CallEnd
import androidx.compose.material3.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.freedom.ui.theme.FreedomTheme
import com.freedom.webrtc.SignalingClient
import com.freedom.webrtc.WebRTCClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.*

class CallScreen : ComponentActivity() {

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var signalingClient: SignalingClient
    private lateinit var webRTCClient: WebRTCClient

    private lateinit var localVideoView: SurfaceViewRenderer
    private lateinit var remoteVideoView: SurfaceViewRenderer
    private lateinit var eglBase: EglBase

    private val peerId = "alice" // Replace with actual username
    private val roomId = "room123"
    private val jwtToken = "your.jwt.token" // Securely load

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        setContent {
            FreedomTheme {
                Box(Modifier.fillMaxSize()) {
                    AndroidView(factory = {
                        localVideoView = SurfaceViewRenderer(it)
                        remoteVideoView = SurfaceViewRenderer(it)
                        eglBase = EglBase.create()

                        localVideoView.init(eglBase.eglBaseContext, null)
                        remoteVideoView.init(eglBase.eglBaseContext, null)
                        localVideoView.setZOrderMediaOverlay(true)

                        val frame = FrameLayout(it)
                        frame.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        frame.addView(remoteVideoView, MATCH_PARENT, MATCH_PARENT)
                        frame.addView(localVideoView, 400, 300)
                        frame
                    })

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { startCall() }) {
                            Icon(Icons.Default.Videocam, contentDescription = "Start", tint = Color.Green)
                        }
                        IconButton(onClick = { endCall() }) {
                            Icon(Icons.Default.CallEnd, contentDescription = "End", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                val granted = result[Manifest.permission.CAMERA] == true &&
                              result[Manifest.permission.RECORD_AUDIO] == true
                if (granted) initWebRTC()
                else finish()
            }

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        if (permissions.all {
                ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            initWebRTC()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun initWebRTC() {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()

        webRTCClient = WebRTCClient(
            context = this,
            peerConnectionFactory = peerConnectionFactory,
            signalingClient = signalingClient
        )

        webRTCClient.initPeerConnection(
            iceServers = iceServers,
            onIceCandidate = { candidate ->
                signalingClient.send("bob", mapOf("type" to "candidate", "candidate" to candidate.sdp))
            },
            onRemoteStream = { stream ->
                val videoTrack = stream.videoTracks.firstOrNull()
                videoTrack?.addSink(remoteVideoView)
            }
        )

        setupLocalMedia()

        signalingClient = SignalingClient(
            peerId = peerId,
            roomId = roomId,
            token = jwtToken
        ) { from, data ->
            CoroutineScope(Dispatchers.Main).launch {
                when (data["type"]) {
                    "offer" -> {
                        val sdp = data["sdp"] as? String ?: return@launch
                        webRTCClient.handleRemoteOffer(sdp) { answer ->
                            signalingClient.send(from, mapOf("type" to "answer", "sdp" to answer.description))
                        }
                    }
                    "answer" -> {
                        val sdp = data["sdp"] as? String ?: return@launch
                        webRTCClient.handleRemoteAnswer(sdp)
                    }
                    "candidate" -> {
                        val candidate = data["candidate"] as? String ?: return@launch
                        val iceCandidate = IceCandidate("video", 0, candidate)
                        webRTCClient.addIceCandidate(iceCandidate)
                    }
                }
            }
        }
    }

    private fun setupLocalMedia() {
        val videoSource = peerConnectionFactory.createVideoSource(false)
        val videoCapturer = createCameraCapturer()
        videoCapturer?.initialize(
            SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext),
            this,
            videoSource.capturerObserver
        )
        videoCapturer?.startCapture(640, 480, 30)

        val localVideoTrack = peerConnectionFactory.createVideoTrack("local_video", videoSource)
        localVideoTrack.addSink(localVideoView)

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val audioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource)

        val stream = peerConnectionFactory.createLocalMediaStream("local_stream")
        stream.addTrack(localVideoTrack)
        stream.addTrack(audioTrack)

        webRTCClient.peerConnection?.addStream(stream)
    }

    private fun startCall() {
        webRTCClient.createOffer { offer ->
            signalingClient.send("bob", mapOf("type" to "offer", "sdp" to offer.description))
        }
    }

    private fun endCall() {
        webRTCClient.close()
        finish()
    }

    private fun createCameraCapturer(): CameraVideoCapturer? {
        val enumerator = Camera2Enumerator(this)
        val deviceNames = enumerator.deviceNames
        for (name in deviceNames) {
            if (enumerator.isFrontFacing(name)) {
                return enumerator.createCapturer(name, null)
            }
        }
        return null
    }
}
