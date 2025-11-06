package com.example.entryexitproject

import androidx.compose.runtime.mutableStateListOf

data class LeaveApplication(
    val id: String = java.util.UUID.randomUUID().toString(),
    val studentName: String,
    val rollNumber: String,
    val degree: String,
    val leavingDate: String,
    val leavingTime: String,
    val returnDate: String,
    val returnTime: String,
    val duration: String,
    val transport: String,
    val block: String,
    val hostel: String,
    val purpose: String,
    val address: String,
    val parentsPhone: String,
    var status: String = "Pending"
)

object LeaveApplicationRepository {
    val leaveApplications = mutableStateListOf<LeaveApplication>()
}