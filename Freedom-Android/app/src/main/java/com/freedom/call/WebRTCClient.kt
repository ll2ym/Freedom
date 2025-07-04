package com.freedom.call

import android.content.Context
import org.webrtc.*

class WebRTCClient(
    private val context: Context,
    private val peerConnectionObserver: PeerConnection.Observer,
    private val sdpObserver: SdpObserver
) {

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var eglBase: EglBase = EglBase.create()

    fun initPeerConnection(iceServers: List<PeerConnection.IceServer>) {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()
        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext, true, true
        )
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, peerConnectionObserver)
            ?: throw IllegalStateException("Failed to create PeerConnection")

        // Create media tracks
        val videoSource = peerConnectionFactory.createVideoSource(false)
        localVideoTrack = peerConnectionFactory.createVideoTrack("video", videoSource)

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource)

        // Add streams
        val mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream")
        mediaStream.addTrack(localVideoTrack)
        mediaStream.addTrack(localAudioTrack)
        peerConnection.addStream(mediaStream)
    }

    fun createOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection.createOffer(sdpObserver, constraints)
    }

    fun createAnswer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection.createAnswer(sdpObserver, constraints)
    }

    fun setLocalDescription(sdp: SessionDescription) {
        peerConnection.setLocalDescription(sdpObserver, sdp)
    }

    fun setRemoteDescription(sdp: SessionDescription) {
        peerConnection.setRemoteDescription(sdpObserver, sdp)
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection.addIceCandidate(candidate)
    }

    fun release() {
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        peerConnection.close()
        peerConnection.dispose()
        peerConnectionFactory.dispose()
    }

    fun getEglBaseContext(): EglBase.Context = eglBase.eglBaseContext
}
