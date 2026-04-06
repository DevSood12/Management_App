//package com.example.login_page.auth
//
//import com.google.firebase.auth.FirebaseAuth
//
//object FirebaseAuthManager {
//
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//
//    fun register(
//        email: String,
//        password: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { onError(it.message ?: "Registration failed") }
//    }
//
//    fun login(
//        email: String,
//        password: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { onError(it.message ?: "Login failed") }
//    }
//}

package com.example.login_page.auth

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun registerUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, "Registration successful")
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Registration failed")
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, "Login successful")
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Login failed")
            }
    }
}
