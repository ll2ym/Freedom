package com.freedom.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

sealed class CallState {
    object Idle : CallState()
    object Calling : CallState()
    object Ringing : CallState()
    object InCall : CallState()
    object Ended : CallState()
    data class Error(val message: String) : CallState()
}

class CallViewModel : ViewModel() {

    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState

    private val _localSdp = MutableStateFlow<SessionDescription?>(null)
    val localSdp: StateFlow<SessionDescription?> = _localSdp

    private val _remoteSdp = MutableStateFlow<SessionDescription?>(null)
    val remoteSdp: StateFlow<SessionDescription?> = _remoteSdp

    private val _iceCandidates = MutableStateFlow<List<IceCandidate>>(emptyList())
    val iceCandidates: StateFlow<List<IceCandidate>> = _iceCandidates

    fun startCall() {
        _callState.value = CallState.Calling
    }

    fun receiveCall() {
        _callState.value = CallState.Ringing
    }

    fun acceptCall() {
        _callState.value = CallState.InCall
    }

    fun endCall() {
        _callState.value = CallState.Ended
        clearCallData()
    }

    fun setLocalSdp(sdp: SessionDescription) {
        _localSdp.value = sdp
    }

    fun setRemoteSdp(sdp: SessionDescription) {
        _remoteSdp.value = sdp
    }

    fun addIceCandidate(candidate: IceCandidate) {
        viewModelScope.launch {
            _iceCandidates.value = _iceCandidates.value + candidate
        }
    }

    fun reportError(message: String) {
        _callState.value = CallState.Error(message)
    }

    private fun clearCallData() {
        _localSdp.value = null
        _remoteSdp.value = null
        _iceCandidates.value = emptyList()
        _callState.value = CallState.Idle
    }
}
