package com.example.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class UserRole(val title: String, val badge: String) {
    STUDENT("Student", "Student Portal"),
    TEACHER("Teacher / Faculty", "Faculty Portal"),
    ADMINISTRATOR("Administrator", "Admin Master Console")
}

data class UserSession(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val studentDbId: Long = 1,
    val isMfaVerified: Boolean = true,
    val avatarInitials: String = "NH"
)

object AuthManager {
    var currentUser by mutableStateOf(
        UserSession(
            id = "USR-STU-001",
            name = "Nahid Hasan",
            email = "nahidhasan5684@gmail.com",
            role = UserRole.STUDENT,
            studentDbId = 1,
            isMfaVerified = true,
            avatarInitials = "NH"
        )
    )

    var isMfaRequiredDialogVisible by mutableStateOf(false)
    var isOfflineMode by mutableStateOf(false)
    var isSyncing by mutableStateOf(false)
    var lastSyncTimestamp by mutableStateOf("Just now")

    fun switchRole(role: UserRole) {
        currentUser = when (role) {
            UserRole.STUDENT -> UserSession(
                id = "USR-STU-001",
                name = "Nahid Hasan",
                email = "nahidhasan5684@gmail.com",
                role = UserRole.STUDENT,
                studentDbId = 1,
                isMfaVerified = true,
                avatarInitials = "NH"
            )
            UserRole.TEACHER -> UserSession(
                id = "USR-TCH-102",
                name = "Prof. Dr. M. Rahman",
                email = "prof.rahman@scorner.ac",
                role = UserRole.TEACHER,
                studentDbId = 1,
                isMfaVerified = true,
                avatarInitials = "MR"
            )
            UserRole.ADMINISTRATOR -> UserSession(
                id = "USR-ADM-999",
                name = "Principal / System Admin",
                email = "admin@scorner.ac",
                role = UserRole.ADMINISTRATOR,
                studentDbId = 1,
                isMfaVerified = true,
                avatarInitials = "AD"
            )
        }
    }

    fun verifyMfaCode(enteredCode: String): Boolean {
        // Valid if 6 digits or default demo code 246810
        val isValid = enteredCode.length == 6
        if (isValid) {
            currentUser = currentUser.copy(isMfaVerified = true)
        }
        return isValid
    }
}
