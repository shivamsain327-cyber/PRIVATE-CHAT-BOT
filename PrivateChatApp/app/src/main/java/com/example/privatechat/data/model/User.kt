package com.example.privatechat.data.model

data class User(
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val statusText: String = "Online & Protected",
    val isOnline: Boolean = true,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)
