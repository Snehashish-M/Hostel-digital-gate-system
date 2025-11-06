package com.example.entryexitproject

import android.content.Context
import androidx.core.content.edit

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
    private const val MASTER_PREFS = "leave_applications_master"
    private const val APPLICATION_IDS = "application_ids"

    fun saveLeaveApplication(context: Context, application: LeaveApplication) {
        // Save the application details
        val applicationPrefs = context.getSharedPreferences(application.id, Context.MODE_PRIVATE)
        applicationPrefs.edit {
            putString("studentName", application.studentName)
            putString("rollNumber", application.rollNumber)
            putString("degree", application.degree)
            putString("leavingDate", application.leavingDate)
            putString("leavingTime", application.leavingTime)
            putString("returnDate", application.returnDate)
            putString("returnTime", application.returnTime)
            putString("duration", application.duration)
            putString("transport", application.transport)
            putString("block", application.block)
            putString("hostel", application.hostel)
            putString("purpose", application.purpose)
            putString("address", application.address)
            putString("parentsPhone", application.parentsPhone)
            putString("status", application.status)
        }

        // Add the application ID to the master list
        val masterPrefs = context.getSharedPreferences(MASTER_PREFS, Context.MODE_PRIVATE)
        val applicationIds = masterPrefs.getStringSet(APPLICATION_IDS, emptySet()) ?: emptySet()
        masterPrefs.edit {
            putStringSet(APPLICATION_IDS, applicationIds + application.id)
        }
    }

    fun getAllLeaveApplications(context: Context): List<LeaveApplication> {
        val masterPrefs = context.getSharedPreferences(MASTER_PREFS, Context.MODE_PRIVATE)
        val applicationIds = masterPrefs.getStringSet(APPLICATION_IDS, emptySet()) ?: emptySet()
        return applicationIds.mapNotNull { id ->
            val appPrefs = context.getSharedPreferences(id, Context.MODE_PRIVATE)
            if (!appPrefs.contains("studentName")) return@mapNotNull null
            LeaveApplication(
                id = id,
                studentName = appPrefs.getString("studentName", "") ?: "",
                rollNumber = appPrefs.getString("rollNumber", "") ?: "",
                degree = appPrefs.getString("degree", "") ?: "",
                leavingDate = appPrefs.getString("leavingDate", "") ?: "",
                leavingTime = appPrefs.getString("leavingTime", "") ?: "",
                returnDate = appPrefs.getString("returnDate", "") ?: "",
                returnTime = appPrefs.getString("returnTime", "") ?: "",
                duration = appPrefs.getString("duration", "") ?: "",
                transport = appPrefs.getString("transport", "") ?: "",
                block = appPrefs.getString("block", "") ?: "",
                hostel = appPrefs.getString("hostel", "") ?: "",
                purpose = appPrefs.getString("purpose", "") ?: "",
                address = appPrefs.getString("address", "") ?: "",
                parentsPhone = appPrefs.getString("parentsPhone", "") ?: "",
                status = appPrefs.getString("status", "Pending") ?: "Pending"
            )
        }
    }

    fun updateLeaveApplicationStatus(context: Context, applicationId: String, newStatus: String) {
        val applicationPrefs = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE)
        applicationPrefs.edit {
            putString("status", newStatus)
        }
    }
}