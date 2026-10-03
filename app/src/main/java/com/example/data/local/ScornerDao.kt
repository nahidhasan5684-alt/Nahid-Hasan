package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CertificateEntity
import com.example.data.model.CourseMaterialEntity
import com.example.data.model.DeadlineAlertEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScornerDao {

    // Students
    @Query("SELECT * FROM students ORDER BY id ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // Transcripts
    @Query("SELECT * FROM transcripts WHERE studentId = :studentId ORDER BY id DESC")
    fun getTranscriptsByStudent(studentId: Long): Flow<List<TranscriptEntity>>

    @Query("SELECT * FROM transcripts ORDER BY id DESC")
    fun getAllTranscripts(): Flow<List<TranscriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscript(transcript: TranscriptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscripts(transcripts: List<TranscriptEntity>)

    @Delete
    suspend fun deleteTranscript(transcript: TranscriptEntity)

    // Certificates
    @Query("SELECT * FROM certificates WHERE studentId = :studentId ORDER BY id DESC")
    fun getCertificatesByStudent(studentId: Long): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates ORDER BY id DESC")
    fun getAllCertificates(): Flow<List<CertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: CertificateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificates(certificates: List<CertificateEntity>)

    @Delete
    suspend fun deleteCertificate(certificate: CertificateEntity)

    // Fee Payments
    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeesByStudent(studentId: Long): Flow<List<FeePaymentEntity>>

    @Query("SELECT * FROM fee_payments ORDER BY id DESC")
    fun getAllFees(): Flow<List<FeePaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: FeePaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFees(fees: List<FeePaymentEntity>)

    @Update
    suspend fun updateFee(fee: FeePaymentEntity)

    // Deadline Alerts
    @Query("SELECT * FROM deadline_alerts WHERE studentId = :studentId ORDER BY isCompleted ASC, id ASC")
    fun getDeadlinesByStudent(studentId: Long): Flow<List<DeadlineAlertEntity>>

    @Query("SELECT * FROM deadline_alerts ORDER BY isCompleted ASC, id ASC")
    fun getAllDeadlines(): Flow<List<DeadlineAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: DeadlineAlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadlines(deadlines: List<DeadlineAlertEntity>)

    @Update
    suspend fun updateDeadline(deadline: DeadlineAlertEntity)

    @Delete
    suspend fun deleteDeadline(deadline: DeadlineAlertEntity)

    // Course Materials
    @Query("SELECT * FROM course_materials ORDER BY id ASC")
    fun getAllMaterials(): Flow<List<CourseMaterialEntity>>

    @Query("SELECT * FROM course_materials WHERE title LIKE '%' || :query || '%' OR courseCode LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchMaterials(query: String): Flow<List<CourseMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: CourseMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<CourseMaterialEntity>)

    @Query("UPDATE course_materials SET downloadsCount = downloadsCount + 1 WHERE id = :id")
    suspend fun incrementMaterialDownload(id: Long)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationItemEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // Resets / Counts
    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}
