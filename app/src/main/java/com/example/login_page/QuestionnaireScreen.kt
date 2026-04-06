package com.example.login_page

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.login_page.firebase.QuestionnaireRepository
import com.google.android.gms.location.*

@Composable
fun QuestionnaireScreen(navController: NavController) {

    val context = LocalContext.current
    val repository = remember { QuestionnaireRepository() }

    var submitting by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var totalScore by remember { mutableIntStateOf(0) }

    val questions = listOf(
        "I take my seizure medicine exactly as prescribed",
        "I take my seizure medicine at the same time every day",
        "I do not miss doses of my seizure medicine",
        "I refill my seizure medicine before it runs out",
        "I understand what my seizure medicine is for",
        "I know the side effects of my seizure medicine",
        "I tell my doctor if I have side effects from my medicine",
        "I carry my seizure medicine when I travel",
        "I follow my doctor’s instructions about my medicine",
        "I remind myself when it is time to take my medicine",
        "I know what type of seizures I have",
        "I know what causes my seizures",
        "I know what to do when I have a seizure",
        "I know when I should call my doctor",
        "I know when I should go to the hospital",
        "I keep track of my seizures",
        "I write down when my seizures happen",
        "I share seizure information with my doctor",
        "I avoid activities that increase my risk of injury",
        "I take safety precautions at home",
        "I take safety precautions when I go out",
        "I avoid situations where seizures could be dangerous",
        "I tell others what to do if I have a seizure",
        "I wear or carry medical identification",
        "I plan ahead for emergencies",
        "I feel confident managing my safety",
        "I get enough sleep regularly",
        "I avoid things that trigger my seizures",
        "I manage stress in healthy ways",
        "I eat regular and healthy meals",
        "I avoid alcohol or recreational drugs",
        "I exercise safely",
        "I balance rest and activity",
        "I take care of my overall health",
        "I stay calm when I feel a seizure coming",
        "I know what to do after a seizure",
        "I ask for help when I need it",
        "I feel confident managing my epilepsy"
    )

    val answers = remember {
        mutableStateListOf(*Array(questions.size) { 0 })
    }

    fun allAnswered() = answers.all { it in 1..5 }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (!granted) {
            submitting = false
            Toast.makeText(context, "Location permission required", Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }

        fetchLocation(
            context,
            onSuccess = { lat, lng ->
                repository.submitQuestionnaire(
                    answers = answers.toList(),
                    latitude = lat,
                    longitude = lng,

                    onSuccess = {
                        submitting = false
                        navController.navigate("map/$lat/$lng") {
                            popUpTo("questionnaire") { inclusive = true }
                        }
                    },

                    onError = {
                        submitting = false
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                )
            },

            onError = {
                submitting = false
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Self-Management Questionnaire",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF1F3F8)
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Instructions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("1 → Strongly Disagree (Worst)")
                Text("2 → Disagree")
                Text("3 → Neutral")
                Text("4 → Agree")
                Text("5 → Strongly Agree (Best)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        questions.forEachIndexed { index, question ->

            Text("${index + 1}. $question", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                (1..5).forEach { value ->
                    OutlinedButton(
                        onClick = { answers[index] = value },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor =
                                if (answers[index] == value)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                else Color.Transparent
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(value.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showScore) {
            Text(
                text = "Your Score: $totalScore / ${questions.size * 5}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {

                if (!allAnswered()) {
                    Toast.makeText(context, "Please answer all questions", Toast.LENGTH_LONG).show()
                    return@Button
                }

                if (!showScore) {
                    totalScore = answers.sum()
                    showScore = true
                } else {
                    submitting = true

                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = !submitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    submitting -> "Getting Location..."
                    !showScore -> "Calculate Score"
                    else -> "Show Location on Map"
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchLocation(
    context: android.content.Context,
    onSuccess: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {

    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation
        .addOnSuccessListener { location ->

            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {

                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    1000
                ).setMaxUpdates(1).build()

                fusedClient.requestLocationUpdates(
                    request,
                    object : LocationCallback() {

                        override fun onLocationResult(result: LocationResult) {
                            val loc = result.lastLocation

                            if (loc != null) {
                                onSuccess(loc.latitude, loc.longitude)
                            } else {
                                onError("Unable to get location")
                            }

                            fusedClient.removeLocationUpdates(this)
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }
        .addOnFailureListener {
            onError("Location failed")
        }
}