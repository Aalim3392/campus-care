package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    STUDENT,
    FACULTY,
    HOD
}

enum class StressStatus {
    NORMAL,
    WATCHLIST_WARNING,
    CRITICAL_FACULTY_ESCALATION
}

enum class AlertType {
    EARLY_WARNING_STUDENT,
    FACULTY_CRISIS_ESCALATION
}

@Entity(tableName = "check_ins")
data class CheckInRecord(
    @PrimaryKey val id: String,
    val date: String,             // e.g. "2025-04-28"
    val dayOfWeek: String,        // e.g. "Mon"
    val mood: String,             // "Great", "Good", "Okay", "Low", "Stressed"
    val sleepHours: Float,        // e.g. 7.5f
    val waterGlasses: Int,        // e.g. 6 glasses
    val studyHours: Float,        // e.g. 4.5f
    val stressLevel: Int,         // 1 to 5
    val userEmail: String = "student@kkwagh.edu.in",
    val userName: String = "Aarav Sharma",
    val userRole: String = "STUDENT",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) {
    val waterLiters: Float
        get() = (waterGlasses * 0.25f)

    val sleepFormatted: String
        get() {
            val h = sleepHours.toInt()
            val m = ((sleepHours - h) * 60).toInt()
            return if (m > 0) "${h}h ${m}m" else "${h}h"
        }

    val studyFormatted: String
        get() {
            val h = studyHours.toInt()
            val m = ((studyHours - h) * 60).toInt()
            return if (m > 0) "${h}h ${m}m" else "${h}h"
        }
}

@Entity(tableName = "support_requests")
data class SupportRequestRecord(
    @PrimaryKey val id: String,
    val topic: String,
    val message: String,
    val date: String,
    val userEmail: String = "student@kkwagh.edu.in",
    val userName: String = "Aarav Sharma",
    val status: String = "Submitted privately",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

data class UserProfile(
    val id: String = "std_101",
    val name: String = "Aarav Sharma",
    val department: String = "Computer Engineering Dept",
    val section: String = "Comp Eng · Div A / Year 3",
    val email: String = "aarav.sharma@kkwagh.edu.in",
    val role: UserRole = UserRole.STUDENT,
    val streakDays: Int = 4,
    val weeklyCheckinsCount: Int = 5,
    val isLoggedIn: Boolean = true
)

data class DepartmentStudent(
    val id: String,
    val name: String,
    val rollNo: String,
    val email: String,
    val division: String,
    val lastMood: String,
    val sleepHours: Float,
    val waterGlasses: Int,
    val stressLevel: Int,
    val stressStatus: StressStatus,
    val aiAdvice: String,
    val facultyNotified: Boolean = false
)

data class StressAlert(
    val id: String,
    val studentName: String,
    val studentEmail: String,
    val stressLevel: Int,
    val alertType: AlertType,
    val aiMessage: String,
    val facultyInformedName: String = "Prof. Dr. P. B. Sharma (Mental Health & Counseling Lead)",
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatSender {
    USER,
    ASSISTANT
}

data class SupabaseConfig(
    val url: String = "",
    val anonKey: String = "",
    val isConnected: Boolean = false,
    val lastSyncMessage: String = "Local encrypted storage active"
)
