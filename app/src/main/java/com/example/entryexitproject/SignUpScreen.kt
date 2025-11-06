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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, onSignUp: () -> Unit) {
    var studentName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Student") }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userTypes = listOf("Student", "Chief Warden")
    var userTypeExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
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
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sign Up", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(32.dp))

                    ExposedDropdownMenuBox(expanded = userTypeExpanded, onExpandedChange = { userTypeExpanded = !userTypeExpanded }) {
                        OutlinedTextField(
                            value = userType,
                            onValueChange = {},
                            label = { Text("User Type") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userTypeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = userTypeExpanded, onDismissRequest = { userTypeExpanded = false }) {
                            userTypes.forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = { userType = it; userTypeExpanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text(if (userType == "Student") "Student Name" else "Warden Name") },
                        isError = showError && studentName.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (userType == "Student") {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = rollNumber,
                            onValueChange = { rollNumber = it },
                            label = { Text("Roll Number") },
                            isError = showError && rollNumber.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = showError && email.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        isError = showError && phoneNumber.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showError && password.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError) {
                        Text(
                            text = "All fields are required",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val isStudentAndRollNumberBlank = userType == "Student" && rollNumber.isBlank()
                            if (studentName.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() || isStudentAndRollNumberBlank) {
                                showError = true
                            } else {
                                showError = false
                                val userPrefs = context.getSharedPreferences(email, Context.MODE_PRIVATE)
                                userPrefs.edit {
                                    putString("studentName", studentName)
                                    putString("rollNumber", if (userType == "Student") rollNumber else "N/A")
                                    putString("email", email)
                                    putString("phoneNumber", phoneNumber)
                                    putString("password", password)
                                    putString("userType", userType)
                                }
                                val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                                sessionPrefs.edit {
                                    putString("currentUserEmail", email)
                                }
                                onSignUp()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF663399))
                    ) {
                        Text("Sign Up")
                    }
                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Already have an account? Sign In")
                    }
                }
            }
        }
    }
}