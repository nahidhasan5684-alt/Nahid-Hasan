package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val name: String,
    val className: String,
    val sscGpa: Double,
    val college: String,
    val address: String,
    val phone: String,
    val email: String,
    val guardianContact: String,
    val enrollmentYear: String = "2024-2026",
    val currentGpa: Double = 5.00,
    val attendanceRate: Int = 96,
    val isSynced: Boolean = true
)

@Entity(tableName = "transcripts")
data class TranscriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subject: String,
    val semester: String,
    val marks: Double,
    val gradePoint: Double,
    val letterGrade: String,
    val credits: Int,
    val examDate: String,
    val remarks: String
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String,
    val issuer: String,
    val category: String,
    val issueDate: String,
    val verificationId: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "fee_payments")
data class FeePaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String,
    val amount: Double,
    val dueDate: String,
    val paidDate: String? = null,
    val status: String, // "PAID", "PENDING", "OVERDUE"
    val transactionId: String? = null,
    val paymentMethod: String? = null
)

@Entity(tableName = "deadline_alerts")
data class DeadlineAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String,
    val course: String,
    val deadlineDate: String,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val type: String // "ASSIGNMENT", "EXAM", "PROJECT", "FEE"
)

@Entity(tableName = "course_materials")
data class CourseMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val courseCode: String,
    val category: String,
    val instructor: String,
    val fileType: String,
    val fileSize: String,
    val downloadsCount: Int = 0
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val category: String // "DEADLINE", "GRADE", "PAYMENT", "SYSTEM"
)
