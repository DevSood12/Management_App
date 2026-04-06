package com.example.login_page.firebase

import com.example.login_page.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue

class FirebaseUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun saveUser(
        name: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        val currentUser = auth.currentUser ?: run {
            onResult(false)
            return
        }

        val user = User(
            uid = currentUser.uid,
            name = name,
            email = currentUser.email ?: "",
            phone = phone
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .set(user, SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateLastLogin(
        onComplete: (Boolean) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .update("lastLogin", FieldValue.serverTimestamp())
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
