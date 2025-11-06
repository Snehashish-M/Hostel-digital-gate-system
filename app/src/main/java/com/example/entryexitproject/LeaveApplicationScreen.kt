package com.example.entryexitproject

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveApplicationScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val currentUserEmail = sessionPrefs.getString("currentUserEmail", "") ?: ""
    val userPrefs = context.getSharedPreferences(currentUserEmail, Context.MODE_PRIVATE)
    val studentName = userPrefs.getString("studentName", "") ?: ""
    val rollNumber = userPrefs.getString("rollNumber", "") ?: ""

    var degree by remember { mutableStateOf("Please Select") }
    var duration by remember { mutableStateOf("") }
    var transport by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }
    var hostel by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var parentsPhone by remember { mutableStateOf("") }

    var showLeavingDatePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }
    var showLeavingTimePicker by remember { mutableStateOf(false) }
    var showReturnTimePicker by remember { mutableStateOf(false) }

    val leavingDatePickerState = rememberDatePickerState()
    val returnDatePickerState = rememberDatePickerState()
    val leavingTimePickerState = rememberTimePickerState()
    val returnTimePickerState = rememberTimePickerState()

    var selectedLeavingDate by remember { mutableStateOf<Long?>(null) }
    var selectedReturnDate by remember { mutableStateOf<Long?>(null) }
    var selectedLeavingTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedReturnTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val degrees = listOf("B.Tech", "M.Tech", "Ph.D", "JRF")
    var degreeExpanded by remember { mutableStateOf(false) }
    var submissionStatus by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Leave Application", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = studentName, onValueChange = {}, label = { Text("Name") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = rollNumber, onValueChange = {}, label = { Text("Student Roll Number") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = degreeExpanded, onExpandedChange = { degreeExpanded = !degreeExpanded }) {
                OutlinedTextField(
                    value = degree,
                    onValueChange = {},
                    label = { Text("Degree") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = degreeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = degreeExpanded, onDismissRequest = { degreeExpanded = false }) {
                    degrees.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { degree = it; degreeExpanded = false })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Details of Leave/Absence", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showLeavingDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(selectedLeavingDate?.let { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it)) } ?: "Leaving Date")
                }
                OutlinedButton(onClick = { showLeavingTimePicker = true }, modifier = Modifier.weight(1f)) {
                     Text(selectedLeavingTime?.let { "%02d:%02d".format(it.first, it.second) } ?: "Leaving Time")
                }
            }
             Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showReturnDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(selectedReturnDate?.let { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it)) } ?: "Return Date")
                }
                 OutlinedButton(onClick = { showReturnTimePicker = true }, modifier = Modifier.weight(1f)) {
                     Text(selectedReturnTime?.let { "%02d:%02d".format(it.first, it.second) } ?: "Return Time")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration of Leave") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(value = transport, onValueChange = { transport = it }, label = { Text("Mode of Transport") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = block, onValueChange = { block = it }, label = { Text("Block/Floor") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = hostel, onValueChange = { hostel = it }, label = { Text("Hostel Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = purpose, onValueChange = { purpose = it }, label = { Text("Purpose of Leave") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address during leave") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(value = parentsPhone, onValueChange = { parentsPhone = it }, label = { Text("Parent's Phone during leave") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))

            if (submissionStatus.isNotEmpty()) {
                Text(submissionStatus, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(onClick = {
                val application = LeaveApplication(
                    studentName = studentName,
                    rollNumber = rollNumber,
                    degree = degree,
                    leavingDate = selectedLeavingDate?.let { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                    leavingTime = selectedLeavingTime?.let { "%02d:%02d".format(it.first, it.second) } ?: "",
                    returnDate = selectedReturnDate?.let { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                    returnTime = selectedReturnTime?.let { "%02d:%02d".format(it.first, it.second) } ?: "",
                    duration = duration,
                    transport = transport,
                    block = block,
                    hostel = hostel,
                    purpose = purpose,
                    address = address,
                    parentsPhone = parentsPhone
                )
                LeaveApplicationRepository.saveLeaveApplication(context, application)
                submissionStatus = "Leave application submitted successfully!"
                 Toast.makeText(context, "Leave application submitted!", Toast.LENGTH_SHORT).show()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("Submit", color = Color.White)
            }
        }
    }

    if (showLeavingDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showLeavingDatePicker = false },
            confirmButton = { Button(onClick = { selectedLeavingDate = leavingDatePickerState.selectedDateMillis; showLeavingDatePicker = false }) { Text("OK") } },
            dismissButton = { Button(onClick = { showLeavingDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = leavingDatePickerState) }
    }

    if (showReturnDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showReturnDatePicker = false },
            confirmButton = { Button(onClick = { selectedReturnDate = returnDatePickerState.selectedDateMillis; showReturnDatePicker = false }) { Text("OK") } },
            dismissButton = { Button(onClick = { showReturnDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = returnDatePickerState) }
    }
    
    if (showLeavingTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showLeavingTimePicker = false },
            onConfirm = { selectedLeavingTime = Pair(leavingTimePickerState.hour, leavingTimePickerState.minute); showLeavingTimePicker = false },
        ) { TimePicker(state = leavingTimePickerState) }
    }

    if (showReturnTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showReturnTimePicker = false },
            onConfirm = { selectedReturnTime = Pair(returnTimePickerState.hour, returnTimePickerState.minute); showReturnTimePicker = false },
        ) { TimePicker(state = returnTimePickerState) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = { content() },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
