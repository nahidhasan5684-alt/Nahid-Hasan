package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CertificateEntity
import com.example.data.model.CourseMaterialEntity
import com.example.data.model.DeadlineAlertEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        TranscriptEntity::class,
        CertificateEntity::class,
        FeePaymentEntity::class,
        DeadlineAlertEntity::class,
        CourseMaterialEntity::class,
        NotificationItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ScornerDatabase : RoomDatabase() {
    abstract fun scornerDao(): ScornerDao

    companion object {
        @Volatile
        private var INSTANCE: ScornerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ScornerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScornerDatabase::class.java,
                    "scorner_academic_db"
                )
                .addCallback(ScornerDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ScornerDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.scornerDao())
                }
            }
        }

        suspend fun populateInitialData(dao: ScornerDao) {
            // Seed Students
            val students = listOf(
                StudentEntity(
                    id = 1,
                    studentId = "SC-2026-0842",
                    name = "Nahid Hasan",
                    className = "Class 12 (Science)",
                    sscGpa = 5.00,
                    college = "Dhaka College",
                    address = "Plot 14, Road 7, Mirpur DOHS, Dhaka, Bangladesh",
                    phone = "+880 1712-345678",
                    email = "nahidhasan5684@gmail.com",
                    guardianContact = "+880 1819-987654 (Md. Rafiqul Hasan - Father)",
                    enrollmentYear = "2024-2026",
                    currentGpa = 5.00,
                    attendanceRate = 98,
                    isSynced = true
                ),
                StudentEntity(
                    id = 2,
                    studentId = "SC-2026-0843",
                    name = "Ayesha Siddiqua",
                    className = "Class 12 (Science)",
                    sscGpa = 5.00,
                    college = "Viqarunnisa Noon College",
                    address = "Baily Road, Dhaka, Bangladesh",
                    phone = "+880 1911-223344",
                    email = "ayesha.siddiqua@edu.scorner.ac",
                    guardianContact = "+880 1711-556677 (Dr. Anwar Siddiqui)",
                    enrollmentYear = "2024-2026",
                    currentGpa = 4.95,
                    attendanceRate = 95,
                    isSynced = true
                ),
                StudentEntity(
                    id = 3,
                    studentId = "SC-2026-0844",
                    name = "Tanvir Ahmed",
                    className = "Class 11 (Business Studies)",
                    sscGpa = 4.88,
                    college = "Notre Dame College",
                    address = "Motijheel, Dhaka, Bangladesh",
                    phone = "+880 1622-445566",
                    email = "tanvir.ahmed@edu.scorner.ac",
                    guardianContact = "+880 1555-889900 (Mrs. Rasheda Begum)",
                    enrollmentYear = "2025-2027",
                    currentGpa = 4.80,
                    attendanceRate = 92,
                    isSynced = true
                )
            )
            dao.insertStudents(students)

            // Seed Transcripts for Student 1
            val transcripts = listOf(
                TranscriptEntity(
                    studentId = 1,
                    subject = "Higher Mathematics",
                    semester = "Midterm Examination",
                    marks = 96.0,
                    gradePoint = 5.00,
                    letterGrade = "A+",
                    credits = 3,
                    examDate = "Aug 20, 2026",
                    remarks = "Top in cohort; exceptional proofs in Calculus"
                ),
                TranscriptEntity(
                    studentId = 1,
                    subject = "Physics (Theoretical & Lab)",
                    semester = "Midterm Examination",
                    marks = 94.5,
                    gradePoint = 5.00,
                    letterGrade = "A+",
                    credits = 3,
                    examDate = "Aug 23, 2026",
                    remarks = "Outstanding performance in Electromagnetism"
                ),
                TranscriptEntity(
                    studentId = 1,
                    subject = "Chemistry",
                    semester = "Midterm Examination",
                    marks = 91.0,
                    gradePoint = 5.00,
                    letterGrade = "A+",
                    credits = 3,
                    examDate = "Aug 26, 2026",
                    remarks = "Mastery of Organic Reaction mechanisms"
                ),
                TranscriptEntity(
                    studentId = 1,
                    subject = "Biology",
                    semester = "Midterm Examination",
                    marks = 88.0,
                    gradePoint = 5.00,
                    letterGrade = "A+",
                    credits = 3,
                    examDate = "Aug 29, 2026",
                    remarks = "Thorough analytical diagrams and Genetics answers"
                ),
                TranscriptEntity(
                    studentId = 1,
                    subject = "English Language & Literature",
                    semester = "Midterm Examination",
                    marks = 85.0,
                    gradePoint = 4.80,
                    letterGrade = "A",
                    credits = 2,
                    examDate = "Sep 02, 2026",
                    remarks = "High quality essay structure and critical vocabulary"
                ),
                TranscriptEntity(
                    studentId = 1,
                    subject = "ICT & Computer Programming",
                    semester = "Midterm Examination",
                    marks = 98.0,
                    gradePoint = 5.00,
                    letterGrade = "A+",
                    credits = 2,
                    examDate = "Sep 05, 2026",
                    remarks = "Flawless code architecture and algorithms test"
                )
            )
            dao.insertTranscripts(transcripts)

            // Seed Certificates for Student 1
            val certificates = listOf(
                CertificateEntity(
                    studentId = 1,
                    title = "National Physics Olympiad Gold Medalist",
                    issuer = "Bangladesh Physics Olympiad Committee",
                    category = "Academic Olympiad",
                    issueDate = "July 2026",
                    verificationId = "BDPhO-2026-GOLD-091",
                    isVerified = true
                ),
                CertificateEntity(
                    studentId = 1,
                    title = "Inter-College Robotics & AI Hackathon Winner",
                    issuer = "BUET Robotics Society & IEEE",
                    category = "Tech & Innovation",
                    issueDate = "May 2026",
                    verificationId = "BUET-HACK-2026-042",
                    isVerified = true
                ),
                CertificateEntity(
                    studentId = 1,
                    title = "National Parliamentary Debate Championship",
                    issuer = "National Debate Federation (NDF)",
                    category = "Oratory & Debate",
                    issueDate = "March 2026",
                    verificationId = "NDF-DEB-2026-881",
                    isVerified = true
                ),
                CertificateEntity(
                    studentId = 1,
                    title = "Duke of Edinburgh International Bronze Award",
                    issuer = "The Duke of Edinburgh's Award Foundation",
                    category = "Leadership & Service",
                    issueDate = "January 2026",
                    verificationId = "DofE-INT-2026-773",
                    isVerified = true
                )
            )
            dao.insertCertificates(certificates)

            // Seed Fee Payments
            val fees = listOf(
                FeePaymentEntity(
                    studentId = 1,
                    title = "Fall Semester 2026 Tuition Fee",
                    amount = 12500.0,
                    dueDate = "Oct 25, 2026",
                    paidDate = null,
                    status = "PENDING",
                    transactionId = null,
                    paymentMethod = null
                ),
                FeePaymentEntity(
                    studentId = 1,
                    title = "Advanced Science Lab & Equipment Fee",
                    amount = 3200.0,
                    dueDate = "Nov 10, 2026",
                    paidDate = null,
                    status = "PENDING",
                    transactionId = null,
                    paymentMethod = null
                ),
                FeePaymentEntity(
                    studentId = 1,
                    title = "Midterm Examination Registration Fee",
                    amount = 2500.0,
                    dueDate = "Aug 15, 2026",
                    paidDate = "Aug 10, 2026",
                    status = "PAID",
                    transactionId = "TXN-BK-992147",
                    paymentMethod = "bKash Digital Payment"
                ),
                FeePaymentEntity(
                    studentId = 1,
                    title = "Annual Digital Library & Portal Access",
                    amount = 1800.0,
                    dueDate = "Jul 20, 2026",
                    paidDate = "Jul 18, 2026",
                    status = "PAID",
                    transactionId = "TXN-DB-881290",
                    paymentMethod = "Visa Debit Card"
                )
            )
            dao.insertFees(fees)

            // Seed Deadline Alerts
            val deadlines = listOf(
                DeadlineAlertEntity(
                    studentId = 1,
                    title = "Physics Lab Practical File & Report Submission",
                    course = "Physics 201",
                    deadlineDate = "Tomorrow, 11:59 PM",
                    priority = "HIGH",
                    isCompleted = false,
                    type = "ASSIGNMENT"
                ),
                DeadlineAlertEntity(
                    studentId = 1,
                    title = "Calculus Integration Problem Set #5",
                    course = "Higher Math",
                    deadlineDate = "Oct 08, 2026",
                    priority = "HIGH",
                    isCompleted = false,
                    type = "ASSIGNMENT"
                ),
                DeadlineAlertEntity(
                    studentId = 1,
                    title = "Chemistry Organic Synthesis Term Project",
                    course = "Chemistry 202",
                    deadlineDate = "Oct 15, 2026",
                    priority = "MEDIUM",
                    isCompleted = false,
                    type = "PROJECT"
                ),
                DeadlineAlertEntity(
                    studentId = 1,
                    title = "Fall Semester Tuition Fee Clearance Deadline",
                    course = "Accounts & Bursar",
                    deadlineDate = "Oct 25, 2026",
                    priority = "HIGH",
                    isCompleted = false,
                    type = "FEE"
                ),
                DeadlineAlertEntity(
                    studentId = 1,
                    title = "English Debate Speech Draft Submission",
                    course = "English Literature",
                    deadlineDate = "Oct 02, 2026",
                    priority = "LOW",
                    isCompleted = true,
                    type = "ASSIGNMENT"
                )
            )
            dao.insertDeadlines(deadlines)

            // Seed Course Materials
            val materials = listOf(
                CourseMaterialEntity(
                    title = "Calculus & Vector Analysis Complete Lecture Notes",
                    courseCode = "MATH-201",
                    category = "Lecture Notes",
                    instructor = "Prof. Dr. M. Rahman",
                    fileType = "PDF",
                    fileSize = "5.4 MB",
                    downloadsCount = 142
                ),
                CourseMaterialEntity(
                    title = "Quantum Physics & Electrodynamics Formula Sheet",
                    courseCode = "PHY-202",
                    category = "Cheat Sheet",
                    instructor = "Dr. S. K. Roy",
                    fileType = "PDF",
                    fileSize = "2.1 MB",
                    downloadsCount = 285
                ),
                CourseMaterialEntity(
                    title = "Organic Reaction Mechanisms & Named Reactions Guide",
                    courseCode = "CHEM-202",
                    category = "Textbook",
                    instructor = "Dr. Farhana Yasmin",
                    fileType = "PDF",
                    fileSize = "14.8 MB",
                    downloadsCount = 198
                ),
                CourseMaterialEntity(
                    title = "Previous 5 Years College Model Test & HSC Board Papers",
                    courseCode = "GEN-ALL",
                    category = "Question Bank",
                    instructor = "Academic Council",
                    fileType = "PDF",
                    fileSize = "28.5 MB",
                    downloadsCount = 512
                ),
                CourseMaterialEntity(
                    title = "Python Data Structures & Algorithm Lab Practical Manual",
                    courseCode = "ICT-301",
                    category = "Lab Manual",
                    instructor = "Engr. Tanvir Morshed",
                    fileType = "DOCX",
                    fileSize = "3.2 MB",
                    downloadsCount = 175
                )
            )
            dao.insertMaterials(materials)

            // Seed Notifications
            val notifications = listOf(
                NotificationItemEntity(
                    title = "Midterm Results Published",
                    message = "Your Midterm examination transcripts are now available. Verified GPA: 5.00.",
                    category = "GRADE",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30
                ),
                NotificationItemEntity(
                    title = "Physics Lab Practical Deadline Alert",
                    message = "Physics Lab File submission is due tomorrow by 11:59 PM.",
                    category = "DEADLINE",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                NotificationItemEntity(
                    title = "Fee Payment Reminder",
                    message = "Fall Semester 2026 tuition fee invoice has been generated (৳12,500). Due Oct 25.",
                    category = "PAYMENT",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 360
                ),
                NotificationItemEntity(
                    title = "System Synchronized",
                    message = "Offline database successfully synced with SCORNER institutional cloud.",
                    category = "SYSTEM",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 720
                )
            )
            dao.insertNotifications(notifications)
        }
    }
}
