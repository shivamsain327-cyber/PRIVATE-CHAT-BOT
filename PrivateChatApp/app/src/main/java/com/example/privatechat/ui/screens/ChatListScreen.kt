package com.example.privatechat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.privatechat.data.model.User
import com.example.privatechat.data.repository.ChatRepository
import com.example.privatechat.ui.theme.AccentNeon
import com.example.privatechat.ui.theme.DeepCharcoal
import com.example.privatechat.ui.theme.EmeraldGreen
import com.example.privatechat.ui.theme.SurfaceDark
import com.example.privatechat.ui.theme.SurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chatRepository: ChatRepository,
    onFriendSelected: (User) -> Unit,
    onLockApp: () -> Unit
) {
    val friends by chatRepository.friends.collectAsState()
    val currentUser by chatRepository.currentUser.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PrivateChat 🔒",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hi, ${currentUser.displayName} (@${currentUser.username})",
                            fontSize = 12.sp,
                            color = AccentNeon
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLockApp) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock App",
                            tint = EmeraldGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldGreen,
                contentColor = DeepCharcoal
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Friend")
            }
        },
        containerColor = DeepCharcoal
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                // Banner for features
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = EmeraldGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Private Calls & Media Active",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Photos, Videos aur HD Audio/Video Call 100% private hain.",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(friends) { friend ->
                FriendChatItem(
                    friend = friend,
                    onClick = { onFriendSelected(friend) }
                )
                HorizontalDivider(
                    color = Color.DarkGray.copy(alpha = 0.4f),
                    thickness = 0.6.dp,
                    modifier = Modifier.padding(start = 72.dp)
                )
            }
        }

        if (showAddDialog) {
            AddFriendDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { username, name ->
                    chatRepository.addFriend(username, name)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun FriendChatItem(
    friend: User,
    onClick: () -> Unit
) {
    val isBot = friend.id == "ai_bot_companion"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isBot) EmeraldGreen else SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            if (isBot) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot",
                    tint = DeepCharcoal,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = friend.displayName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = friend.displayName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = if (isBot) "AI BOT" else "Online",
                    fontSize = 11.sp,
                    color = if (isBot) AccentNeon else EmeraldGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = friend.statusText,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (username: String, name: String) -> Unit
) {
    var friendUsername by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Friend Add Karein") },
        text = {
            Column {
                Text("Apne friend ka naam aur username dalein:", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text("Friend ka Naam") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = friendUsername,
                    onValueChange = { friendUsername = it },
                    label = { Text("Username (@handle)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (friendName.isNotBlank() && friendUsername.isNotBlank()) {
                        onAdd(friendUsername.trim(), friendName.trim())
                    }
                }
            ) {
                Text("Add Karein", color = EmeraldGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = SurfaceDark
    )
}
