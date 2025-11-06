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
                val sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE)
                val isLoggedIn = sharedPreferences.contains("studentName")
                val startDestination = if (isLoggedIn) "dashboard" else "signup"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("signup") {
                        SignUpScreen(onSignUp = { navController.navigate("dashboard") })
                    }
                    composable("dashboard") {
                        DashboardScreen(navController = navController)
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryExitApp(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE)
    val studentName = sharedPreferences.getString("studentName", "") ?: ""
    val rollNumber = sharedPreferences.getString("rollNumber", "") ?: ""
    val email = sharedPreferences.getString("email", "") ?: ""
    val phoneNumber = sharedPreferences.getString("phoneNumber", "") ?: ""

    var location by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Personal Details") },
                                onClick = {
                                    showDetailsDialog = true
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    sharedPreferences.edit { clear() }
                                    navController.navigate("signup") { popUpTo(0) }
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Personal Details") },
            text = {
                Column {
                    Text("Name: $studentName")
                    Text("Roll Number: $rollNumber")
                    Text("Email: $email")
                    Text("Phone Number: $phoneNumber")
                }
            },
            confirmButton = {
                Button(onClick = { showDetailsDialog = false }, shape = RoundedCornerShape(8.dp)) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(8.dp)
        )
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
