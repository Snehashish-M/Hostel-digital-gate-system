package com.example.entryexitproject

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun DayScholarEntryScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_details", Context.MODE_PRIVATE)
    val studentName = sharedPreferences.getString("studentName", "") ?: ""
    val rollNumber = sharedPreferences.getString("rollNumber", "") ?: ""
    val email = sharedPreferences.getString("email", "") ?: ""
    val phoneNumber = sharedPreferences.getString("phoneNumber", "") ?: ""

    val studentDetails = "Name: $studentName, Roll Number: $rollNumber, Type: Day Scholar"
    val qrCodeBitmap = remember { generateQrCode(studentDetails) }

    Scaffold {
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
