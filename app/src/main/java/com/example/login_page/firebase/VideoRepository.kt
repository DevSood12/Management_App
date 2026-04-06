package com.example.login_page.firebase

import android.net.Uri
import com.example.login_page.model.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class VideoRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun uploadVideo(
        videoUri: Uri,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val user = auth.currentUser
        if (user == null) {
            onError("User not logged in")
            return
        }

        val videoId = UUID.randomUUID().toString()

        val storageRef = storage.reference
            .child("videos/${user.uid}/$videoId.mp4")

        val formattedTime = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        storageRef.putFile(videoUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                    // 🔥 FETCH USER NAME
                    firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->

                            val userName = document.getString("name") ?: "Unknown"

                            val data = hashMapOf(
                                "id" to videoId,
                                "userId" to user.uid,
                                "userName" to userName,   // ✅ NEW
                                "videoUrl" to downloadUrl.toString(),
                                "description" to description,
                                "timestamp" to System.currentTimeMillis(),
                                "readableTime" to formattedTime  // ✅ NEW
                            )

                            firestore.collection("videos")
                                .document(videoId)
                                .set(data)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener {
                                    onError(it.message ?: "Firestore error")
                                }
                        }
                }

            }
            .addOnFailureListener {
                onError(it.message ?: "Upload failed")
            }
    }
}