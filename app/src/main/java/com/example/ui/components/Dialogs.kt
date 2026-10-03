package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.auth.AuthManager
import com.example.data.model.FeePaymentEntity
import com.example.data.model.StudentEntity
import com.example.localization.LanguageManager
import com.example.ui.theme.*

@Composable
fun MfaVerificationDialog(
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var otpCode by remember { mutableStateOf("246810") } // pre-filled demo code for convenience
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("mfa_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "MFA Security",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LanguageManager.getString("mfa_verification"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A 6-digit authentication token was generated for ${AuthManager.currentUser.email}. Enter it below to confirm academic role access.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    label = { Text("6-Digit OTP Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mfa_otp_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (AuthManager.verifyMfaCode(otpCode)) {
                            onVerified()
                        } else {
                            errorMessage = "Invalid code. Please enter 6 digits."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("mfa_verify_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify & Authenticate", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { otpCode = "246810"; errorMessage = null },
                    modifier = Modifier.testTag("mfa_resend_button")
                ) {
                    Text("Resend Verification Code", color = PrimaryLight)
                }
            }
        }
    }
}

@Composable
fun FeeReceiptDialog(
    fee: FeePaymentEntity,
    student: StudentEntity?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("receipt_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Receipt Header
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Paid",
                        tint = SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SCORNER OFFICIAL RECEIPT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PrimaryBlue,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Educational Expense Transaction Voucher",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                ReceiptRow(label = "Transaction ID", value = fee.transactionId ?: "TXN-BK-992147", isMono = true)
                ReceiptRow(label = "Date & Time", value = fee.paidDate ?: "Today, 11:30 AM")
                ReceiptRow(label = "Student Name", value = student?.name ?: "Nahid Hasan")
                ReceiptRow(label = "Student ID", value = student?.studentId ?: "SC-2026-0842")
                ReceiptRow(label = "Institution", value = student?.college ?: "Dhaka College")
                ReceiptRow(label = "Fee Purpose", value = fee.title)
                ReceiptRow(label = "Payment Gateway", value = fee.paymentMethod ?: "bKash Digital Payment")
                ReceiptRow(label = "Payment Status", value = "COMPLETED / PAID", isHighlighted = true)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Paid Amount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "৳${fee.amount.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = SuccessGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "SCORNER Fee Voucher\nStudent: ${student?.name}\nID: ${student?.studentId}\nFee: ${fee.title}\nAmount: ৳${fee.amount}\nTxn: ${fee.transactionId}\nStatus: PAID"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isMono: Boolean = false, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlighted) SuccessGreen else MaterialTheme.colorScheme.onSurface,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@Composable
fun SocialShareDialog(
    shareText: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    fun launchShare(extraText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, extraText)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("social_share_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Promote & Share Milestone",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Share academic achievements, transcripts & certificates across your professional network.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Share Buttons
                ShareOptionItem(
                    name = "LinkedIn Network",
                    subtitle = "Post verified certificate & GPA milestone",
                    icon = Icons.Filled.Work,
                    color = Color(0xFF0077B5),
                    onClick = { launchShare("🎓 Excited to share my academic progress on SCORNER:\n$shareText\n#Education #AcademicExcellence #SCORNER") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShareOptionItem(
                    name = "WhatsApp & Messaging",
                    subtitle = "Send official transcript overview to mentors/parents",
                    icon = Icons.Filled.Send,
                    color = Color(0xFF25D366),
                    onClick = { launchShare("SCORNER Academic Record Overview:\n$shareText") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShareOptionItem(
                    name = "Twitter / X",
                    subtitle = "Broadcast research & Olympiad achievements",
                    icon = Icons.Filled.Tag,
                    color = Color(0xFF1DA1F2),
                    onClick = { launchShare("Milestone verified on SCORNER academic portal: $shareText") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShareOptionItem(
                    name = "Copy Credential Link / Other Apps",
                    subtitle = "Use Android system share sheet",
                    icon = Icons.Filled.Share,
                    color = PrimaryBlue,
                    onClick = { launchShare("SCORNER Verified Milestone:\n$shareText\nhttps://scorner.ac/verify/nahid-hasan-2026") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun ShareOptionItem(
    name: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = name, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, className: String, sscGpa: Double, college: String, address: String, phone: String, email: String, guardianContact: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Class 12 (Science)") }
    var sscGpaText by remember { mutableStateOf("5.00") }
    var college by remember { mutableStateOf("Dhaka College") }
    var address by remember { mutableStateOf("Dhaka, Bangladesh") }
    var phone by remember { mutableStateOf("+880 1712-") }
    var email by remember { mutableStateOf("") }
    var guardianContact by remember { mutableStateOf("+880 1819-") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_student_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Input Student Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Register student name, class, SSC GPA, college, address and contacts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = className,
                    onValueChange = { className = it },
                    label = { Text("Class / Grade *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_class_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sscGpaText,
                    onValueChange = { sscGpaText = it },
                    label = { Text("SSC GPA (e.g. 5.00) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_ssc_gpa_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / Institution *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_college_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address *") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().testTag("student_address_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Student Contact Phone *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_phone_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Student Email *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_email_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = guardianContact,
                    onValueChange = { guardianContact = it },
                    label = { Text("Guardian Contact Information *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_guardian_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val gpa = sscGpaText.toDoubleOrNull()
                            if (name.isBlank() || className.isBlank() || college.isBlank() || address.isBlank()) {
                                errorMessage = "Please fill in all mandatory fields."
                            } else if (gpa == null || gpa <= 0.0 || gpa > 5.0) {
                                errorMessage = "SSC GPA must be between 1.0 and 5.0"
                            } else {
                                onSave(name, className, gpa, college, address, phone, email, guardianContact)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_student_button")
                    ) {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun EditStudentProfileDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onSave: (StudentEntity) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var className by remember { mutableStateOf(student.className) }
    var sscGpaText by remember { mutableStateOf(student.sscGpa.toString()) }
    var college by remember { mutableStateOf(student.college) }
    var address by remember { mutableStateOf(student.address) }
    var phone by remember { mutableStateOf(student.phone) }
    var email by remember { mutableStateOf(student.email) }
    var guardianContact by remember { mutableStateOf(student.guardianContact) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_student_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Edit Student Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = className,
                    onValueChange = { className = it },
                    label = { Text("Class / Grade") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sscGpaText,
                    onValueChange = { sscGpaText = it },
                    label = { Text("SSC GPA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / Institution") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = guardianContact,
                    onValueChange = { guardianContact = it },
                    label = { Text("Guardian Contact") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val gpa = sscGpaText.toDoubleOrNull() ?: student.sscGpa
                            val updated = student.copy(
                                name = name,
                                className = className,
                                sscGpa = gpa,
                                college = college,
                                address = address,
                                phone = phone,
                                email = email,
                                guardianContact = guardianContact
                            )
                            onSave(updated)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

@Composable
fun AddTranscriptDialog(
    onDismiss: () -> Unit,
    onSave: (subject: String, semester: String, marks: Double, gradePoint: Double, letterGrade: String, credits: Int, remarks: String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("Semester 2 Examination") }
    var marksText by remember { mutableStateOf("92") }
    var creditsText by remember { mutableStateOf("3") }
    var remarks by remember { mutableStateOf("High distinction with honors") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_transcript_dialog")
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Record Academic Transcript", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Higher Math, Physics)") },
                    modifier = Modifier.fillMaxWidth().testTag("transcript_subject_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester / Term") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = marksText,
                    onValueChange = { marksText = it },
                    label = { Text("Marks (Out of 100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = creditsText,
                    onValueChange = { creditsText = it },
                    label = { Text("Credits") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Faculty Remarks / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            val marks = marksText.toDoubleOrNull() ?: 80.0
                            val credits = creditsText.toIntOrNull() ?: 3
                            val gradePoint = when {
                                marks >= 80 -> 5.00
                                marks >= 70 -> 4.00
                                marks >= 60 -> 3.50
                                marks >= 50 -> 3.00
                                marks >= 40 -> 2.00
                                else -> 0.00
                            }
                            val letter = when {
                                marks >= 80 -> "A+"
                                marks >= 70 -> "A"
                                marks >= 60 -> "A-"
                                marks >= 50 -> "B"
                                marks >= 40 -> "C"
                                else -> "F"
                            }
                            if (subject.isNotBlank()) {
                                onSave(subject, semester, marks, gradePoint, letter, credits, remarks)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_transcript_button")
                    ) {
                        Text("Add Record")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCertificateDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, issuer: String, category: String, verificationId: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic Olympiad") }
    var verificationId by remember { mutableStateOf("CERT-" + (1000..9999).random()) }

    val categories = listOf("Academic Olympiad", "Coding & Tech", "Debate & Oratory", "Sports & Athletics", "Leadership")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_certificate_dialog")
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Add Extracurricular Certification", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Certificate Title *") },
                    modifier = Modifier.fillMaxWidth().testTag("cert_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = issuer,
                    onValueChange = { issuer = it },
                    label = { Text("Issuing Organization *") },
                    modifier = Modifier.fillMaxWidth().testTag("cert_issuer_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Category", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = verificationId,
                    onValueChange = { verificationId = it },
                    label = { Text("Verification Credential ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && issuer.isNotBlank()) {
                                onSave(title, issuer, category, verificationId)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_certificate_button")
                    ) {
                        Text("Save Certificate")
                    }
                }
            }
        }
    }
}

@Composable
fun AddDeadlineDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, course: String, deadlineDate: String, priority: String, type: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("Physics 201") }
    var deadlineDate by remember { mutableStateOf("Oct 18, 2026, 11:59 PM") }
    var priority by remember { mutableStateOf("HIGH") }
    var type by remember { mutableStateOf("ASSIGNMENT") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_deadline_dialog")
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Schedule Deadline / Task Alert", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task / Exam Title *") },
                    modifier = Modifier.fillMaxWidth().testTag("deadline_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Course / Department") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deadlineDate,
                    onValueChange = { deadlineDate = it },
                    label = { Text("Due Date & Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Priority Level", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, course, deadlineDate, priority, type)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_deadline_button")
                    ) {
                        Text("Set Alert")
                    }
                }
            }
        }
    }
}
