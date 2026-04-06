package com.example.login_page.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuestionnaireRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun submitQuestionnaire(
        answers: List<Int>,
        latitude: Double,
        longitude: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("User not logged in")
            return
        }

        val totalScore = answers.sum()

        val formattedTime = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        // 🔥 FETCH USER NAME
        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                val userName = document.getString("name") ?: "Unknown"

                val data = hashMapOf(
                    "userId" to user.uid,
                    "userName" to userName,   // ✅ NEW
                    "answers" to answers,
                    "totalScore" to totalScore,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "timestamp" to System.currentTimeMillis(),
                    "readableTime" to formattedTime  // ✅ NEW
                )

                firestore.collection("questionnaires")
                    .add(data)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener {
                        onError(it.message ?: "Submission failed")
                    }
            }
    }
}