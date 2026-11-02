package com.example.privatechat.data.model

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallStatus {
    IDLE,
    CALLING,
    RINGING,
    CONNECTED,
    ENDED
}

data class CallSession(
    val callId: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val callType: CallType = CallType.VIDEO,
    val status: CallStatus = CallStatus.IDLE,
    val isMicMuted: Boolean = false,
    val isVideoDisabled: Boolean = false,
    val isFrontCamera: Boolean = true,
    val durationSeconds: Long = 0L,
    val startedAt: Long = 0L
)
