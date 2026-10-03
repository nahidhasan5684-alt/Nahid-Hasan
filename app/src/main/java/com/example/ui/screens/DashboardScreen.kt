package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.data.model.DeadlineAlertEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import com.example.localization.LanguageManager
import com.example.ui.components.ProductivityRadialGauge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalyticsData
import com.example.ui.viewmodel.ScornerViewModel

@Composable
fun DashboardScreen(
    viewModel: ScornerViewModel,
    student: StudentEntity?,
    transcripts: List<TranscriptEntity>,
    certificates: List<CertificateEntity>,
    deadlines: List<DeadlineAlertEntity>,
    analytics: AnalyticsData,
    onNavigateToTranscripts: () -> Unit,
    onNavigateToFees: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 1. Student Profile Header Card
        item {
            StudentProfileHeroCard(
                student = student,
                onEditClick = { viewModel.showEditStudentDialog.value = true },
                onShareClick = {
                    val summary = "Student: ${student?.name ?: "Nahid Hasan"}\nClass: ${student?.className}\nSSC GPA: ${student?.sscGpa} / 5.00\nCurrent GPA: ${analytics.calculatedGpa}\nCollege: ${student?.college}\nAddress: ${student?.address}\nContact: ${student?.phone}"
                    viewModel.shareTargetText.value = summary
                }
            )
        }

        // 2. Quick Action Bar
        item {
            QuickActionsBar(
                onRegisterStudent = { viewModel.showAddStudentDialog.value = true },
                onAddTranscript = { viewModel.showAddTranscriptDialog.value = true },
                onAddCertificate = { viewModel.showAddCertificateDialog.value = true },
                onAddDeadline = { viewModel.showAddDeadlineDialog.value = true },
                onPayTuition = onNavigateToFees
            )
        }

        // 3. Key Metrics Summary Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Current GPA",
                    value = String.format("%.2f", analytics.calculatedGpa),
                    subtitle = "Scale 5.00",
                    badge = "Top 2%",
                    badgeColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = LanguageManager.getString("ssc_gpa"),
                    value = String.format("%.2f", student?.sscGpa ?: 5.00),
                    subtitle = "Board Exam",
                    badge = "Golden",
                    badgeColor = GoldYellow,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pending Fees",
                    value = "৳${analytics.pendingFeeTotal.toInt()}",
                    subtitle = "Tuition Due",
                    badge = if (analytics.pendingFeeTotal > 0) "Pay Now" else "Cleared",
                    badgeColor = if (analytics.pendingFeeTotal > 0) ErrorRed else SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Productivity & Real-time Progress Gauge
        item {
            ProductivityRadialGauge(
                productivityRate = analytics.productivityRate,
                attendanceRate = student?.attendanceRate ?: 96,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 5. Automated Notification & Upcoming Deadlines Card
        item {
            DeadlinesAlertCard(
                deadlines = deadlines,
                onToggleDeadline = { viewModel.toggleDeadline(it) },
                onAddDeadline = { viewModel.showAddDeadlineDialog.value = true }
            )
        }

        // 6. Recent Academic Transcripts Preview
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Academic Transcripts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToTranscripts) {
                            Text("View All (${transcripts.size})", color = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (transcripts.isEmpty()) {
                        Text(
                            text = "No transcripts recorded yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        transcripts.take(3).forEach { tr ->
                            TranscriptRowItem(transcript = tr)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // 7. Extracurricular Certifications Carousel Preview
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Extracurricular Certifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToTranscripts) {
                            Text("See Certs", color = PrimaryBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(certificates) { cert ->
                            CertificateMiniCard(
                                certificate = cert,
                                onShare = {
                                    val text = "🏆 Extracurricular Achievement:\n${cert.title}\nIssued by: ${cert.issuer} (${cert.issueDate})\nVerification: ${cert.verificationId}"
                                    viewModel.shareTargetText.value = text
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileHeroCard(
    student: StudentEntity?,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_hero_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student?.name?.take(2)?.uppercase() ?: "NH",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student?.name ?: "Nahid Hasan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "ID: ${student?.studentId ?: "SC-2026-0842"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${student?.className ?: "Class 12"} • ${student?.college ?: "Dhaka College"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldYellow
                    )
                }

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.testTag("share_hero_button")
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share Profile", tint = Color.White)
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.testTag("edit_hero_button")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Student Address, Phone, Email & Guardian Info
            ProfileDetailRow(
                icon = Icons.Filled.LocationOn,
                label = LanguageManager.getString("address"),
                value = student?.address ?: "Mirpur DOHS, Dhaka, Bangladesh"
            )
            ProfileDetailRow(
                icon = Icons.Filled.Phone,
                label = LanguageManager.getString("contact"),
                value = "${student?.phone ?: "+880 1712-345678"} • ${student?.email ?: "student@scorner.ac"}"
            )
            ProfileDetailRow(
                icon = Icons.Filled.FamilyRestroom,
                label = LanguageManager.getString("guardian_contact"),
                value = student?.guardianContact ?: "+880 1819-987654 (Father)"
            )
        }
    }
}

@Composable
fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun QuickActionsBar(
    onRegisterStudent: () -> Unit,
    onAddTranscript: () -> Unit,
    onAddCertificate: () -> Unit,
    onAddDeadline: () -> Unit,
    onPayTuition: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            ActionButton(
                label = "+ New Student",
                icon = Icons.Filled.PersonAdd,
                color = PrimaryBlue,
                onClick = onRegisterStudent
            )
        }
        item {
            ActionButton(
                label = "+ Transcript",
                icon = Icons.Filled.PostAdd,
                color = SecondaryTeal,
                onClick = onAddTranscript
            )
        }
        item {
            ActionButton(
                label = "+ Certificate",
                icon = Icons.Filled.WorkspacePremium,
                color = TertiaryAmber,
                onClick = onAddCertificate
            )
        }
        item {
            ActionButton(
                label = "+ Deadline",
                icon = Icons.Filled.AlarmAdd,
                color = ErrorRed,
                onClick = onAddDeadline
            )
        }
        item {
            ActionButton(
                label = "Pay Fees",
                icon = Icons.Filled.Payment,
                color = SuccessGreen,
                onClick = onPayTuition
            )
        }
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeadlinesAlertCard(
    deadlines: List<DeadlineAlertEntity>,
    onToggleDeadline: (DeadlineAlertEntity) -> Unit,
    onAddDeadline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageManager.getString("upcoming_deadlines"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onAddDeadline) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Deadline", tint = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (deadlines.isEmpty()) {
                Text(
                    text = "No pending task deadlines. Great job!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                deadlines.take(4).forEach { deadline ->
                    DeadlineItemRow(
                        deadline = deadline,
                        onToggle = { onToggleDeadline(deadline) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun DeadlineItemRow(
    deadline: DeadlineAlertEntity,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (deadline.isCompleted) Slate100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = deadline.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deadline.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (deadline.isCompleted) Slate700 else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${deadline.course} • ${deadline.deadlineDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (deadline.priority) {
                    "HIGH" -> ErrorRed.copy(alpha = 0.15f)
                    "MEDIUM" -> WarningOrange.copy(alpha = 0.15f)
                    else -> InfoBlue.copy(alpha = 0.15f)
                }
            ) {
                Text(
                    text = deadline.priority,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (deadline.priority) {
                        "HIGH" -> ErrorRed
                        "MEDIUM" -> WarningOrange
                        else -> InfoBlue
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TranscriptRowItem(transcript: TranscriptEntity) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transcript.subject, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("${transcript.semester} • ${transcript.marks} Marks (${transcript.credits} Cr)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryBlue
            ) {
                Text(
                    text = "${transcript.letterGrade} (${transcript.gradePoint})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CertificateMiniCard(
    certificate: CertificateEntity,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TertiaryAmber.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = certificate.category,
                        color = TertiaryAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = certificate.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = certificate.issuer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ID: ${certificate.verificationId}",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
