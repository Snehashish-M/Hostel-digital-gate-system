package com.example.entryexitproject

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiefWardenDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionPrefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val currentUserEmail = sessionPrefs.getString("currentUserEmail", "") ?: ""
    val userPrefs = context.getSharedPreferences(currentUserEmail, Context.MODE_PRIVATE)
    val wardenName = userPrefs.getString("studentName", "") ?: ""
    val email = userPrefs.getString("email", "") ?: ""
    val phoneNumber = userPrefs.getString("phoneNumber", "") ?: ""

    var menuExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    var allApplications by remember { mutableStateOf(LeaveApplicationRepository.getAllLeaveApplications(context)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Leave Applications", fontWeight = FontWeight.Bold) },
                actions = {
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
                                    sessionPrefs.edit { clear() }
                                    navController.navigate("login") { popUpTo(0) }
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(allApplications) { application ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Student: ${application.studentName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Roll Number: ${application.rollNumber}")
                        Text("Degree: ${application.degree}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Leaving: ${application.leavingDate} at ${application.leavingTime}")
                        Text("Returning: ${application.returnDate} at ${application.returnTime}")
                        Text("Duration: ${application.duration}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Purpose: ${application.purpose}")
                        Text("Status: ${application.status}", color = if (application.status == "Pending") Color.Blue else if (application.status == "Approved") Color(0xFF4CAF50) else Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (application.status == "Pending") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { 
                                    LeaveApplicationRepository.updateLeaveApplicationStatus(context, application.id, "Approved")
                                    allApplications = LeaveApplicationRepository.getAllLeaveApplications(context)
                                }) {
                                    Text("Approve")
                                }
                                Button(onClick = { 
                                    LeaveApplicationRepository.updateLeaveApplicationStatus(context, application.id, "Rejected")
                                    allApplications = LeaveApplicationRepository.getAllLeaveApplications(context)
                                }) {
                                    Text("Reject")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Personal Details") },
            text = {
                Column {
                    Text("Name: $wardenName")
                    Text("Email: $email")
                    Text("Phone Number: $phoneNumber")
                }
            },
            confirmButton = {
                Button(onClick = { showDetailsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
