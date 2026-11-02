package com.example.privatechat.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.privatechat.data.model.CallSession
import com.example.privatechat.data.model.CallStatus
import com.example.privatechat.data.model.CallType
import com.example.privatechat.data.repository.ChatRepository
import com.example.privatechat.ui.theme.AccentNeon
import com.example.privatechat.ui.theme.DangerRed
import com.example.privatechat.ui.theme.DeepCharcoal
import com.example.privatechat.ui.theme.EmeraldGreen
import com.example.privatechat.ui.theme.SurfaceDark
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    chatRepository: ChatRepository,
    onCallEnded: () -> Unit
) {
    val activeCall by chatRepository.activeCall.collectAsState()

    // Handle call termination
    LaunchedEffect(activeCall) {
        if (activeCall == null || activeCall?.status == CallStatus.ENDED) {
            onCallEnded()
        }
    }

    val call = activeCall ?: return

    var durationSec by remember { mutableLongStateOf(0L) }
    LaunchedEffect(call.status) {
        if (call.status == CallStatus.CONNECTED) {
            while (true) {
                delay(1000)
                durationSec++
            }
        }
    }

    val minutes = durationSec / 60
    val seconds = durationSec % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
    ) {
        if (call.callType == CallType.VIDEO) {
            // Video Call Layout
            VideoCallLayout(
                call = call,
                timerString = timerString,
                onToggleMic = { chatRepository.toggleMic() },
                onToggleVideo = { chatRepository.toggleVideo() },
                onSwitchCamera = { chatRepository.switchCamera() },
                onEndCall = { chatRepository.endCall() }
            )
        } else {
            // Audio Call Layout
            AudioCallLayout(
                call = call,
                timerString = timerString,
                onToggleMic = { chatRepository.toggleMic() },
                onEndCall = { chatRepository.endCall() }
            )
        }
    }
}

@Composable
fun VideoCallLayout(
    call: CallSession,
    timerString: String,
    onToggleMic: () -> Unit,
    onToggleVideo: () -> Unit,
    onSwitchCamera: () -> Unit,
    onEndCall: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Remote Video Feed Background
        if (!call.isVideoDisabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E2638), Color(0xFF0D1117))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = call.receiverName.take(1).uppercase(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (call.status == CallStatus.CONNECTED) "HD Video Connected" else "Ringing...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Video Paused",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }

        // Floating Local PIP Camera Window (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(width = 110.dp, height = 150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(2.dp, EmeraldGreen, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = if (call.isFrontCamera) "Front" else "Rear",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        // Top Status Header
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 20.dp)
        ) {
            Text(
                text = call.receiverName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (call.status == CallStatus.CONNECTED) "Encrypted $timerString" else "Connecting...",
                    fontSize = 12.sp,
                    color = AccentNeon
                )
            }
        }

        // Bottom Controls Dock
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            shape = RoundedCornerShape(32.dp),
            color = SurfaceDark.copy(alpha = 0.9f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button
                IconButton(
                    onClick = onToggleMic,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (call.isMicMuted) DangerRed else Color.DarkGray)
                ) {
                    Icon(
                        imageVector = if (call.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute Mic",
                        tint = Color.White
                    )
                }

                // Camera Switch Button
                IconButton(
                    onClick = onSwitchCamera,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }

                // Video Toggle Button
                IconButton(
                    onClick = onToggleVideo,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (call.isVideoDisabled) DangerRed else Color.DarkGray)
                ) {
                    Icon(
                        imageVector = if (call.isVideoDisabled) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Toggle Video",
                        tint = Color.White
                    )
                }

                // End Call Button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DangerRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AudioCallLayout(
    call: CallSession,
    timerString: String,
    onToggleMic: () -> Unit,
    onEndCall: () -> Unit
) {
    // Pulse animation for audio call
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Encrypted Call",
                    fontSize = 12.sp,
                    color = EmeraldGreen
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = call.receiverName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (call.status == CallStatus.CONNECTED) timerString else "Calling...",
                fontSize = 15.sp,
                color = Color.LightGray
            )
        }

        // Center Avatar with Glowing Rings
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp * pulseScale)
                    .clip(CircleShape)
                    .background(EmeraldGreen.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen.copy(alpha = 0.3f))
                    .border(2.dp, AccentNeon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = call.receiverName.take(1).uppercase(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            IconButton(
                onClick = onToggleMic,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (call.isMicMuted) DangerRed else SurfaceDark)
            ) {
                Icon(
                    imageVector = if (call.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = Color.White
                )
            }

            // End Call Button
            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(DangerRed)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Speaker Button
            IconButton(
                onClick = { /* Toggle speaker */ },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker",
                    tint = Color.White
                )
            }
        }
    }
}
