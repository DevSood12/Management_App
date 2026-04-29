package com.example.login_page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.login_page.ui.theme.Login_PageTheme
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.login_page.auth.FirebaseAuthManager
import com.example.login_page.firebase.FirebaseUserRepository
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*





class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            Login_PageTheme {
                AppNavigation()
            }
        }
    }
}

/* ---------- NAVIGATION ---------- */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    val startDestination =
        if (auth.currentUser != null) "video"
        else "welcome"

    NavHost(navController, startDestination = startDestination) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("video") { VideoUploadScreen(navController) }
        composable("questionnaire") { QuestionnaireScreen(navController) }
        composable("map/{lat}/{lng}") { backStackEntry ->
            val lat = backStackEntry.arguments!!.getString("lat")!!.toDouble()
            val lng = backStackEntry.arguments!!.getString("lng")!!.toDouble()
            MapScreen(latitude = lat, longitude = lng)
        }
    }
}

/* ---------- HEADER ---------- */
@Composable
fun AppHeader(navController: NavController? = null) {

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Management App",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
            if (navController != null) {
                TextButton(
                    onClick = {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                        navController.navigate("login") {
                            popUpTo("video") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
@Composable
fun WelcomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7D2FE),
                        Color(0xFFE0E7FF),
                        Color(0xFFEAB3BD),
                        Color(0xFFB3DCCC)
                    )
                )
            )
    ) {

        AppHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Welcome",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Management App",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ✅ Image 1 — Card fits image perfectly
            Image(
                painter = painterResource(R.drawable.welcome_image),
                contentDescription = "Main Logo",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Image 2 — Banner card fits image (no box look)
            Image(
                painter = painterResource(R.drawable.second_image),
                contentDescription = "Secondary Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp
                )
            }
        }
    }
}
@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authManager = remember { FirebaseAuthManager() }
    val userRepository = remember { FirebaseUserRepository() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7D2FE), // soft blue
                        Color(0xFFE0E7FF),
                        Color(0xFFD9F5EA)  // mint
                    )
                )
            )
    ) {

        AppHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Login to continue",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            Image(
                painter = painterResource(R.drawable.second_image),
                contentDescription = "Login Illustration",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 20.dp),
                contentScale = ContentScale.Fit
            )

            // EMAIL FIELD
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PASSWORD FIELD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // LOGIN BUTTON
            Button(
                onClick = {
                    authManager.loginUser(
                        email = email,
                        password = password
                    ) { success: Boolean, message: String ->

                        if (success) {
                            userRepository.updateLastLogin { _ ->
                                navController.navigate("profile") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("Login", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("New user? Register")
            }
        }
    }
}
@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val authManager = remember { FirebaseAuthManager() }
    val userRepository = remember { FirebaseUserRepository() }

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
    ) {

        AppHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NAME
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // PHONE
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                        error = "Please fill all fields"
                        return@Button
                    }

                    loading = true
                    error = ""

                    authManager.registerUser(email, password) { success, message ->
                        if (success) {

                            userRepository.saveUser(name, phone) { saved ->
                                loading = false

                                if (saved) {
                                    navController.navigate("profile") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                } else {
                                    error = "Failed to save user data"
                                }
                            }

                        } else {
                            loading = false
                            error = message
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading
            ) {
                Text(if (loading) "Creating..." else "Register")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}
@Composable
fun VideoUploadScreen(navController: NavController) {

    val context = LocalContext.current
    val videoRepository = remember { com.example.login_page.firebase.VideoRepository() }

    var description by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    // 🎥 Video capture launcher
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            videoUri = result.data?.data
        }
    }

    // 🔐 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoLauncher.launch(intent)
        } else {
            Toast.makeText(
                context,
                "Camera permission is required",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7D2FE),
                        Color(0xFFEAB3BD),
                        Color(0xFFB3DCCC)
                    )
                )
            )
    ) {

        AppHeader(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Upload Video",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Record a short video and continue",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 🎥 Video record card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                onClick = {
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        videoLauncher.launch(intent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (videoUri == null)
                            "Tap to record video"
                        else
                            "Video recorded ✅",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📝 Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        text = "Video Description (optional)",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {

                    if (videoUri == null) {
                        Toast.makeText(
                            context,
                            "Please record a video first",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    Toast.makeText(context, "Uploading video...", Toast.LENGTH_SHORT).show()

                    // ✅ USING REPOSITORY NOW
                    videoRepository.uploadVideo(
                        videoUri = videoUri!!,
                        description = description,

                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Video uploaded successfully ✅",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.navigate("questionnaire")
                        },

                        onError = {
                            Toast.makeText(
                                context,
                                "Error: $it",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                },

                enabled = videoUri != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Upload & Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}








