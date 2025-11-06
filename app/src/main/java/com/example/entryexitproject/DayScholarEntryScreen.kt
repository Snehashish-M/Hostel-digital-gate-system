package com.example.entryexitproject

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScholarEntryScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val currentUserEmail = sessionPrefs.getString("currentUserEmail", "") ?: ""
    val userPrefs = context.getSharedPreferences(currentUserEmail, Context.MODE_PRIVATE)
    val studentName = userPrefs.getString("studentName", "") ?: ""
    val rollNumber = userPrefs.getString("rollNumber", "") ?: ""
    val email = userPrefs.getString("email", "") ?: ""
    val phoneNumber = userPrefs.getString("phoneNumber", "") ?: ""

    val studentDetails = "Type: Day Scholar\n"
        .plus("Name: $studentName\n")
        .plus("Roll Number: $rollNumber\n")
        .plus("Phone Number: $phoneNumber")
    val qrCodeBitmap = remember { generateQrCode(studentDetails) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Day's Scholar Entry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Name: $studentName")
            Text("Roll Number: $rollNumber")
            Text("Email: $email")
            Text("Phone Number: $phoneNumber")
            Spacer(modifier = Modifier.height(32.dp))
            Text("Entry/Exit Pass")
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
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
