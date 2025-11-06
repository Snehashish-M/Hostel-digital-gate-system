package com.example.entryexitproject

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bubbleColor = Color.White.copy(alpha = 0.1f)
            drawCircle(
                color = bubbleColor,
                radius = 150.dp.toPx(),
                center = Offset(x = size.width * 0.1f, y = size.height * 0.2f)
            )
            drawCircle(
                color = bubbleColor,
                radius = 200.dp.toPx(),
                center = Offset(x = size.width * 0.9f, y = size.height * 0.8f)
            )
            drawCircle(
                color = bubbleColor,
                radius = 100.dp.toPx(),
                center = Offset(x = size.width * 0.8f, y = size.height * 0.1f)
            )
            drawCircle(
                color = bubbleColor,
                radius = 80.dp.toPx(),
                center = Offset(x = size.width * 0.2f, y = size.height * 0.9f)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sign In", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "Invalid email or password",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val userPrefs = context.getSharedPreferences(email, Context.MODE_PRIVATE)
                            if (userPrefs.contains("password") && userPrefs.getString("password", "") == password) {
                                showError = false
                                val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                                sessionPrefs.edit {
                                    putString("currentUserEmail", email)
                                }
                                val userType = userPrefs.getString("userType", "Student")
                                if (userType == "Student") {
                                    navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
                                } else {
                                    navController.navigate("chief_warden_dashboard") { popUpTo("login") { inclusive = true } }
                                }
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF663399))
                    ) {
                        Text("Login")
                    }
                    TextButton(onClick = { navController.navigate("signup") }) {
                        Text("Don't have an account? Sign Up")
                    }
                }
            }
        }
    }
}