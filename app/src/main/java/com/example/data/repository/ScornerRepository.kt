package com.example.data.repository

import com.example.data.local.ScornerDao
import com.example.data.model.CertificateEntity
import com.example.data.model.CourseMaterialEntity
import com.example.data.model.DeadlineAlertEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ScornerRepository(private val dao: ScornerDao) {

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()

    fun getStudentById(id: Long): Flow<StudentEntity?> = dao.getStudentById(id)

    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)

    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    // Transcripts
    fun getTranscriptsByStudent(studentId: Long): Flow<List<TranscriptEntity>> =
        dao.getTranscriptsByStudent(studentId)

    val allTranscripts: Flow<List<TranscriptEntity>> = dao.getAllTranscripts()

    suspend fun insertTranscript(transcript: TranscriptEntity): Long = dao.insertTranscript(transcript)

    suspend fun deleteTranscript(transcript: TranscriptEntity) = dao.deleteTranscript(transcript)

    // Certificates
    fun getCertificatesByStudent(studentId: Long): Flow<List<CertificateEntity>> =
        dao.getCertificatesByStudent(studentId)

    val allCertificates: Flow<List<CertificateEntity>> = dao.getAllCertificates()

    suspend fun insertCertificate(certificate: CertificateEntity): Long = dao.insertCertificate(certificate)

    suspend fun deleteCertificate(certificate: CertificateEntity) = dao.deleteCertificate(certificate)

    // Fees
    fun getFeesByStudent(studentId: Long): Flow<List<FeePaymentEntity>> =
        dao.getFeesByStudent(studentId)

    val allFees: Flow<List<FeePaymentEntity>> = dao.getAllFees()

    suspend fun insertFee(fee: FeePaymentEntity): Long = dao.insertFee(fee)

    suspend fun updateFee(fee: FeePaymentEntity) = dao.updateFee(fee)

    suspend fun processPayment(feeId: Long, studentId: Long, paymentMethod: String, amount: Double): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val today = dateFormat.format(Date())
        val txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.US)

        val updatedFee = FeePaymentEntity(
            id = feeId,
            studentId = studentId,
            title = "Paid Fee #$feeId",
            amount = amount,
            dueDate = today,
            paidDate = today,
            status = "PAID",
            transactionId = txnId,
            paymentMethod = paymentMethod
        )
        dao.updateFee(updatedFee)

        // Generate automated notification
        dao.insertNotification(
            NotificationItemEntity(
                title = "Payment Successful",
                message = "Your payment of ৳$amount via $paymentMethod was processed. Txn ID: $txnId",
                category = "PAYMENT",
                isRead = false
            )
        )
        return txnId
    }

    // Deadlines
    fun getDeadlinesByStudent(studentId: Long): Flow<List<DeadlineAlertEntity>> =
        dao.getDeadlinesByStudent(studentId)

    val allDeadlines: Flow<List<DeadlineAlertEntity>> = dao.getAllDeadlines()

    suspend fun insertDeadline(deadline: DeadlineAlertEntity): Long {
        val id = dao.insertDeadline(deadline)
        dao.insertNotification(
            NotificationItemEntity(
                title = "New Deadline: ${deadline.title}",
                message = "${deadline.course} deadline scheduled for ${deadline.deadlineDate} (${deadline.priority} Priority).",
                category = "DEADLINE",
                isRead = false
            )
        )
        return id
    }

    suspend fun toggleDeadlineCompleted(deadline: DeadlineAlertEntity) {
        val updated = deadline.copy(isCompleted = !deadline.isCompleted)
        dao.updateDeadline(updated)
    }

    suspend fun deleteDeadline(deadline: DeadlineAlertEntity) = dao.deleteDeadline(deadline)

    // Course Materials
    val allMaterials: Flow<List<CourseMaterialEntity>> = dao.getAllMaterials()

    fun searchMaterials(query: String): Flow<List<CourseMaterialEntity>> {
        return if (query.isBlank()) {
            dao.getAllMaterials()
        } else {
            dao.searchMaterials(query)
        }
    }

    suspend fun insertMaterial(material: CourseMaterialEntity): Long = dao.insertMaterial(material)

    suspend fun incrementMaterialDownload(id: Long) = dao.incrementMaterialDownload(id)

    // Notifications
    val allNotifications: Flow<List<NotificationItemEntity>> = dao.getAllNotifications()

    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)

    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    suspend fun sendBroadcastNotification(title: String, message: String, category: String) {
        dao.insertNotification(
            NotificationItemEntity(
                title = title,
                message = message,
                category = category,
                isRead = false
            )
        )
    }

    // Direct fetch
    suspend fun getStudentDirect(id: Long): StudentEntity? = dao.getStudentByIdDirect(id)
}
