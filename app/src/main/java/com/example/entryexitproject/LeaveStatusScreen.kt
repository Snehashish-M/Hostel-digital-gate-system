package com.example.entryexitproject

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveStatusScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val currentUserEmail = sessionPrefs.getString("currentUserEmail", "") ?: ""
    val userPrefs = context.getSharedPreferences(currentUserEmail, Context.MODE_PRIVATE)
    val studentName = userPrefs.getString("studentName", "") ?: ""

    val allApplications = LeaveApplicationRepository.getAllLeaveApplications(context)
    val studentApplications = allApplications.filter { it.studentName == studentName }
    var showQrDialog by remember { mutableStateOf(false) }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leave Application Status") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(studentApplications) { application ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(getStatusColor(application.status))
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (application.status) {
                                        "Approved" -> Icons.Default.CheckCircle
                                        "Rejected" -> Icons.Default.Close
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = "Status Icon",
                                    tint = getStatusColor(application.status)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = application.status,
                                    fontWeight = FontWeight.Bold,
                                    color = getStatusColor(application.status)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Purpose: ${application.purpose}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Duration: ${application.duration}")
                            if (application.status == "Approved") {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    val qrDetails = "Name: ${application.studentName}\n"
                                        .plus("Roll Number: ${application.rollNumber}\n")
                                        .plus("Leaving: ${application.leavingDate} at ${application.leavingTime}\n")
                                        .plus("Returning: ${application.returnDate} at ${application.returnTime}\n")
                                        .plus("Duration: ${application.duration}\n")
                                        .plus("Address: ${application.address}")
                                    qrCodeBitmap = generateQrCodeForLeave(qrDetails)
                                    showQrDialog = true
                                }) {
                                    Text("View QR Pass")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Leave Pass QR Code") },
            text = {
                qrCodeBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code Pass"
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showQrDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "Approved" -> Color(0xFF4CAF50)
        "Rejected" -> Color.Red
        else -> Color.Blue
    }
}

private fun generateQrCodeForLeave(text: String): Bitmap {
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
