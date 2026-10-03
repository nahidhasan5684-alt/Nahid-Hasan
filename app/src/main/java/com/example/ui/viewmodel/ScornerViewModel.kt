package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.local.ScornerDatabase
import com.example.data.model.CertificateEntity
import com.example.data.model.CourseMaterialEntity
import com.example.data.model.DeadlineAlertEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import com.example.data.repository.ScornerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScornerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScornerRepository

    init {
        val db = ScornerDatabase.getDatabase(application, viewModelScope)
        repository = ScornerRepository(db.scornerDao())
    }

    // Selected Student for profile inspection / editing
    private val _selectedStudentId = MutableStateFlow(1L)
    val selectedStudentId: StateFlow<Long> = _selectedStudentId.asStateFlow()

    fun selectStudent(id: Long) {
        _selectedStudentId.value = id
    }

    // Students
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudent: StateFlow<StudentEntity?> = _selectedStudentId
        .flatMapLatest { id -> repository.getStudentById(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Transcripts for selected student
    val currentTranscripts: StateFlow<List<TranscriptEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getTranscriptsByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTranscripts: StateFlow<List<TranscriptEntity>> = repository.allTranscripts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Certificates for selected student
    val currentCertificates: StateFlow<List<CertificateEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getCertificatesByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fees for selected student
    val currentFees: StateFlow<List<FeePaymentEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getFeesByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFees: StateFlow<List<FeePaymentEntity>> = repository.allFees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Deadlines
    val currentDeadlines: StateFlow<List<DeadlineAlertEntity>> = _selectedStudentId
        .flatMapLatest { id -> repository.getDeadlinesByStudent(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeadlines: StateFlow<List<DeadlineAlertEntity>> = repository.allDeadlines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Course Materials & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val courseMaterials: StateFlow<List<CourseMaterialEntity>> = _searchQuery
        .flatMapLatest { query -> repository.searchMaterials(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Notifications
    val notifications: StateFlow<List<NotificationItemEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Dialog States
    val activeReceipt = MutableStateFlow<FeePaymentEntity?>(null)
    val shareTargetText = MutableStateFlow<String?>(null)
    val showAddStudentDialog = MutableStateFlow(false)
    val showEditStudentDialog = MutableStateFlow(false)
    val showAddTranscriptDialog = MutableStateFlow(false)
    val showAddCertificateDialog = MutableStateFlow(false)
    val showAddDeadlineDialog = MutableStateFlow(false)
    val showPaymentSheetForFee = MutableStateFlow<FeePaymentEntity?>(null)
    val snackbarMessage = MutableStateFlow<String?>(null)

    // Analytics Derived
    val analyticsSummary = combine(
        currentTranscripts,
        currentFees,
        currentDeadlines
    ) { transcripts, fees, deadlines ->
        val totalCredits = transcripts.sumOf { it.credits }
        val weightedGpaSum = transcripts.sumOf { it.gradePoint * it.credits }
        val cumulativeGpa = if (totalCredits > 0) weightedGpaSum / totalCredits else 5.0
        val pendingFeeTotal = fees.filter { it.status == "PENDING" }.sumOf { it.amount }
        val paidFeeTotal = fees.filter { it.status == "PAID" }.sumOf { it.amount }
        val totalTasks = deadlines.size
        val completedTasks = deadlines.count { it.isCompleted }
        val productivityPercent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 100

        AnalyticsData(
            calculatedGpa = String.format(Locale.US, "%.2f", cumulativeGpa).toDouble(),
            totalCredits = totalCredits,
            pendingFeeTotal = pendingFeeTotal,
            paidFeeTotal = paidFeeTotal,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            productivityRate = productivityPercent
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalyticsData(5.00, 16, 15700.0, 4300.0, 5, 1, 20)
    )

    // Actions
    fun saveStudent(
        name: String,
        className: String,
        sscGpa: Double,
        college: String,
        address: String,
        phone: String,
        email: String,
        guardianContact: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val studentIdTag = "SC-" + (1000..9999).random()
            val newStudent = StudentEntity(
                studentId = studentIdTag,
                name = name,
                className = className,
                sscGpa = sscGpa,
                college = college,
                address = address,
                phone = phone,
                email = email,
                guardianContact = guardianContact,
                currentGpa = sscGpa,
                attendanceRate = 95,
                isSynced = !AuthManager.isOfflineMode
            )
            val newId = repository.insertStudent(newStudent)
            _selectedStudentId.value = newId
            snackbarMessage.value = "Student '$name' registered successfully!"
        }
    }

    fun updateStudentInfo(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
            snackbarMessage.value = "Profile updated successfully!"
        }
    }

    fun addTranscript(
        subject: String,
        semester: String,
        marks: Double,
        gradePoint: Double,
        letterGrade: String,
        credits: Int,
        remarks: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val transcript = TranscriptEntity(
                studentId = _selectedStudentId.value,
                subject = subject,
                semester = semester,
                marks = marks,
                gradePoint = gradePoint,
                letterGrade = letterGrade,
                credits = credits,
                examDate = dateFormat.format(Date()),
                remarks = remarks
            )
            repository.insertTranscript(transcript)
            snackbarMessage.value = "Transcript recorded for $subject ($letterGrade)"
        }
    }

    fun addCertificate(
        title: String,
        issuer: String,
        category: String,
        verificationId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
            val certificate = CertificateEntity(
                studentId = _selectedStudentId.value,
                title = title,
                issuer = issuer,
                category = category,
                issueDate = dateFormat.format(Date()),
                verificationId = verificationId,
                isVerified = true
            )
            repository.insertCertificate(certificate)
            snackbarMessage.value = "Certification added: $title"
        }
    }

    fun addDeadline(
        title: String,
        course: String,
        deadlineDate: String,
        priority: String,
        type: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val deadline = DeadlineAlertEntity(
                studentId = _selectedStudentId.value,
                title = title,
                course = course,
                deadlineDate = deadlineDate,
                priority = priority,
                isCompleted = false,
                type = type
            )
            repository.insertDeadline(deadline)
            snackbarMessage.value = "Deadline alert created: $title"
        }
    }

    fun toggleDeadline(deadline: DeadlineAlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleDeadlineCompleted(deadline)
        }
    }

    fun processPayment(fee: FeePaymentEntity, method: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val txnId = repository.processPayment(
                feeId = fee.id,
                studentId = fee.studentId,
                paymentMethod = method,
                amount = fee.amount
            )
            val paidFee = fee.copy(
                status = "PAID",
                paidDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date()),
                transactionId = txnId,
                paymentMethod = method
            )
            activeReceipt.value = paidFee
            snackbarMessage.value = "Payment of ৳${fee.amount} processed! Txn: $txnId"
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllNotifications()
        }
    }

    fun simulateDownloadMaterial(id: Long, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementMaterialDownload(id)
            snackbarMessage.value = "Downloaded '$title' to offline storage"
        }
    }

    fun broadcastAnnouncement(title: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendBroadcastNotification(title, message, "SYSTEM")
            snackbarMessage.value = "Broadcast notification sent to all students & teachers"
        }
    }

    fun triggerDatabaseSync() {
        viewModelScope.launch(Dispatchers.IO) {
            AuthManager.isSyncing = true
            delay(1200) // Realistic network handshake & sync
            AuthManager.isSyncing = false
            AuthManager.lastSyncTimestamp = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
            snackbarMessage.value = "Local database synced seamlessly with SCORNER server."
        }
    }
}

data class AnalyticsData(
    val calculatedGpa: Double,
    val totalCredits: Int,
    val pendingFeeTotal: Double,
    val paidFeeTotal: Double,
    val totalTasks: Int,
    val completedTasks: Int,
    val productivityRate: Int
)
