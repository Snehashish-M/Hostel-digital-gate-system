package com.example.entryexitproject

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.entryexitproject.ui.theme.EntryExitProjectTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EntryExitProjectTheme(darkTheme = false) {
                val navController = rememberNavController()
                val sessionPrefs = getSharedPreferences("session", MODE_PRIVATE)
                val currentUserEmail = sessionPrefs.getString("currentUserEmail", null)

                val startDestination = if (currentUserEmail != null) {
                    val userPrefs = getSharedPreferences(currentUserEmail, MODE_PRIVATE)
                    val userType = userPrefs.getString("userType", "Student")
                    if (userType == "Student") "dashboard" else "chief_warden_dashboard"
                } else {
                    "login"
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("signup") {
                        SignUpScreen(navController = navController, onSignUp = {
                            val signedUpUserType = getSharedPreferences("session", MODE_PRIVATE).getString("userType", "Student")
                            if (signedUpUserType == "Student") {
                                navController.navigate("dashboard") { popUpTo("signup") { inclusive = true } }
                            } else {
                                navController.navigate("chief_warden_dashboard") { popUpTo("signup") { inclusive = true } }
                            }
                        })
                    }
                    composable("dashboard") {
                        DashboardScreen(navController = navController)
                    }
                    composable("chief_warden_dashboard") {
                        ChiefWardenDashboardScreen(navController = navController)
                    }
                    composable("entryexit") {
                        EntryExitApp(navController = navController)
                    }
                    composable("leave_application") {
                        LeaveApplicationScreen()
                    }
                    composable("day_scholar_entry") {
                        DayScholarEntryScreen()
                    }
                    composable("leave_status") {
                        LeaveStatusScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryExitApp(navController: NavController) {
    val context = LocalContext.current
    val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val currentUserEmail = sessionPrefs.getString("currentUserEmail", "") ?: ""
    val userPrefs = context.getSharedPreferences(currentUserEmail, Context.MODE_PRIVATE)
    val studentName = userPrefs.getString("studentName", "") ?: ""
    val rollNumber = userPrefs.getString("rollNumber", "") ?: ""
    val phoneNumber = userPrefs.getString("phoneNumber", "") ?: ""

    var location by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showError by remember { mutableStateOf(false) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                isError = showError,
                shape = RoundedCornerShape(8.dp)
            )
            if (showError) {
                Text(
                    text = "enter the location",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (location.isBlank()) {
                    showError = true
                    qrCodeBitmap = null
                } else {
                    showError = false
                    val studentDetails = "Name: $studentName, Roll Number: $rollNumber, Phone Number: $phoneNumber, Location: $location"
                    qrCodeBitmap = generateQrCode(studentDetails)
                }
            }, shape = RoundedCornerShape(8.dp)) {
                Text("Generate QR Code")
            }
            Spacer(modifier = Modifier.height(16.dp))
            qrCodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

private fun generateQrCode(text: String): Bitmap {
    val width = 512
    val height = 512
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] = if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }
    return bitmap
}
