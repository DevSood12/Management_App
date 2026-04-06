package com.example.login_page.model

import com.google.firebase.Timestamp

data class Video(
    val id: String = "",
    val userId: String = "",
    val videoUrl: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
