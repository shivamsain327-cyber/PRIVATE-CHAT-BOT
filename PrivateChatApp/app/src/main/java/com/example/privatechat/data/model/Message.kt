package com.example.privatechat.data.model

enum class MediaType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO
}

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val mediaUrl: String? = null,
    val mediaType: MediaType = MediaType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = true,
    val isDelivered: Boolean = true,
    val isRead: Boolean = false
)
