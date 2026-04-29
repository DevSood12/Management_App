package com.example.login_page

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val user = auth.currentUser

    LaunchedEffect(Unit) {
        if (user != null) {
            email = user.email ?: ""

            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: "Unknown"
                    phone = document.getString("phone") ?: "Not available"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7D2FE),
                        Color(0xFFE0E7FF),
                        Color(0xFFD9F5EA)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Profile",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Name: $name", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Email: $email", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Phone: $phone", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("video")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Continue to Upload Video")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}