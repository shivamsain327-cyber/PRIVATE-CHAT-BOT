package com.example.privatechat.data.repository

import android.content.Context
import android.net.Uri
import com.example.privatechat.data.model.CallSession
import com.example.privatechat.data.model.CallStatus
import com.example.privatechat.data.model.CallType
import com.example.privatechat.data.model.MediaType
import com.example.privatechat.data.model.Message
import com.example.privatechat.data.model.User
import com.example.privatechat.data.security.EncryptionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository private constructor(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("private_chat_prefs", Context.MODE_PRIVATE)

    // Current Logged-in User
    private val _currentUser = MutableStateFlow(
        User(
            id = sharedPrefs.getString("user_id", UUID.randomUUID().toString()) ?: "user_self",
            username = sharedPrefs.getString("username", "MyProfile") ?: "MyProfile",
            displayName = sharedPrefs.getString("display_name", "You") ?: "You",
            avatarUrl = ""
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Friends List
    private val _friends = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = "ai_bot_companion",
                username = "PrivateAI",
                displayName = "Private AI Bot 🤖",
                avatarUrl = "",
                statusText = "Always active to help & chat"
            ),
            User(
                id = "friend_1",
                username = "rahul_sharma",
                displayName = "Rahul (Best Friend)",
                avatarUrl = "",
                statusText = "Online | End-to-End Encrypted"
            ),
            User(
                id = "friend_2",
                username = "priya_verma",
                displayName = "Priya",
                avatarUrl = "",
                statusText = "Tap to call or send photo"
            ),
            User(
                id = "friend_3",
                username = "amit_patel",
                displayName = "Amit",
                avatarUrl = "",
                statusText = "Available for video call"
            )
        )
    )
    val friends: StateFlow<List<User>> = _friends.asStateFlow()

    // Active Messages Store (Map of friendId -> List<Message>)
    private val _messagesMap = MutableStateFlow<Map<String, List<Message>>>(
        mapOf(
            "ai_bot_companion" to listOf(
                Message(
                    id = "bot_msg_1",
                    senderId = "ai_bot_companion",
                    receiverId = "user_self",
                    text = "Namaste! Main aapka personal AI Assistant hoon. Aap mujhse koi bhi sawal pooch sakte hain, aur yahan apne doston ke sath safe chat, photos, videos aur video calling bhi kar sakte hain!",
                    mediaType = MediaType.TEXT,
                    timestamp = System.currentTimeMillis() - 3600000
                )
            ),
            "friend_1" to listOf(
                Message(
                    id = "msg_1",
                    senderId = "friend_1",
                    receiverId = "user_self",
                    text = "Bhai kaisa hai? Ye chat bot app bahut secure hai, photos & video call sab kaam kar raha hai!",
                    mediaType = MediaType.TEXT,
                    timestamp = System.currentTimeMillis() - 1800000
                )
            )
        )
    )
    val messagesMap: StateFlow<Map<String, List<Message>>> = _messagesMap.asStateFlow()

    // Active Call Session
    private val _activeCall = MutableStateFlow<CallSession?>(null)
    val activeCall: StateFlow<CallSession?> = _activeCall.asStateFlow()

    fun updateProfile(username: String, displayName: String) {
        sharedPrefs.edit()
            .putString("username", username)
            .putString("display_name", displayName)
            .apply()
        _currentUser.value = _currentUser.value.copy(
            username = username,
            displayName = displayName
        )
    }

    fun isPinSetup(): Boolean {
        return sharedPrefs.contains("app_pin_hash")
    }

    fun verifyPin(pin: String): Boolean {
        val stored = sharedPrefs.getString("app_pin_hash", null) ?: return true
        return stored == EncryptionUtils.hashPin(pin)
    }

    fun setPin(pin: String) {
        sharedPrefs.edit()
            .putString("app_pin_hash", EncryptionUtils.hashPin(pin))
            .apply()
    }

    fun addFriend(username: String, displayName: String) {
        val newFriend = User(
            id = "friend_" + UUID.randomUUID().toString().take(6),
            username = username,
            displayName = displayName,
            statusText = "Online | Private Room"
        )
        _friends.value = _friends.value + newFriend
    }

    fun sendMessage(
        receiverId: String,
        text: String,
        mediaUri: Uri? = null,
        mediaType: MediaType = MediaType.TEXT
    ) {
        val currentUserId = _currentUser.value.id
        val messageId = UUID.randomUUID().toString()

        val newMsg = Message(
            id = messageId,
            senderId = currentUserId,
            receiverId = receiverId,
            text = text,
            mediaUrl = mediaUri?.toString(),
            mediaType = mediaType,
            timestamp = System.currentTimeMillis()
        )

        val updatedMap = _messagesMap.value.toMutableMap()
        val currentList = updatedMap[receiverId]?.toMutableList() ?: mutableListOf()
        currentList.add(newMsg)
        updatedMap[receiverId] = currentList
        _messagesMap.value = updatedMap

        // If chatting with AI Bot, generate smart reply!
        if (receiverId == "ai_bot_companion") {
            CoroutineScope(Dispatchers.Default).launch {
                delay(1200)
                val botReplyText = generateBotReply(text, mediaType)
                val botMsg = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = "ai_bot_companion",
                    receiverId = currentUserId,
                    text = botReplyText,
                    mediaType = MediaType.TEXT,
                    timestamp = System.currentTimeMillis()
                )
                val currentAfter = _messagesMap.value.toMutableMap()
                val botChatList = currentAfter["ai_bot_companion"]?.toMutableList() ?: mutableListOf()
                botChatList.add(botMsg)
                currentAfter["ai_bot_companion"] = botChatList
                _messagesMap.value = currentAfter
            }
        }
    }

    private fun generateBotReply(userPrompt: String, mediaType: MediaType): String {
        val lower = userPrompt.lowercase()
        return when {
            mediaType == MediaType.IMAGE -> "Aapne bahut badhiya photo bheji hai! 📸 Maine ise securely save kar liya hai."
            mediaType == MediaType.VIDEO -> "Video receive ho gayi hai! 🎥 Privacy mode ke sath ye encrypted hai."
            lower.contains("hello") || lower.contains("hi") || lower.contains("namaste") ->
                "Hello ji! Kaise hain aap? Main aapka private assistant aur friends chat manager hoon. Aap yahan kisi bhi friend ko call ya photo bhej sakte hain."
            lower.contains("call") || lower.contains("video") ->
                "Aap kisi bhi friend ke chat room me upar bane Camera (Video Call) ya Phone (Audio Call) icon par click karke direct call start kar sakte hain!"
            lower.contains("private") || lower.contains("security") || lower.contains("safe") ->
                "Aapki chat 100% private hai! Isme AES-256 encryption aur PIN lock protection enabled hai."
            else ->
                "Main samajh gaya! Aapka message: \"$userPrompt\". Aap friends ko add karke unke sath text, photo, video aur high-quality calling kar sakte hain!"
        }
    }

    // Call Controls
    fun startCall(friend: User, callType: CallType) {
        val session = CallSession(
            callId = UUID.randomUUID().toString(),
            callerId = _currentUser.value.id,
            callerName = _currentUser.value.displayName,
            receiverId = friend.id,
            receiverName = friend.displayName,
            callType = callType,
            status = CallStatus.CALLING,
            startedAt = System.currentTimeMillis()
        )
        _activeCall.value = session

        // Simulate friend answering after 2 seconds for demo/preview
        CoroutineScope(Dispatchers.Default).launch {
            delay(2500)
            if (_activeCall.value?.callId == session.callId) {
                _activeCall.value = _activeCall.value?.copy(status = CallStatus.CONNECTED)
            }
        }
    }

    fun toggleMic() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isMicMuted = !current.isMicMuted)
    }

    fun toggleVideo() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isVideoDisabled = !current.isVideoDisabled)
    }

    fun switchCamera() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isFrontCamera = !current.isFrontCamera)
    }

    fun endCall() {
        _activeCall.value = _activeCall.value?.copy(status = CallStatus.ENDED)
        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            _activeCall.value = null
        }
    }

    companion object {
        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(context: Context): ChatRepository {
            return instance ?: synchronized(this) {
                instance ?: ChatRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
