package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TranscriptEntity
import com.example.localization.LanguageManager
import com.example.ui.components.GradeDistributionChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalyticsData
import com.example.ui.viewmodel.ScornerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptsAndCertificatesScreen(
    viewModel: ScornerViewModel,
    student: StudentEntity?,
    transcripts: List<TranscriptEntity>,
    certificates: List<CertificateEntity>,
    analytics: AnalyticsData,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Academic Transcripts (${transcripts.size})", "Certifications (${certificates.size})")

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        viewModel.showAddTranscriptDialog.value = true
                    } else {
                        viewModel.showAddCertificateDialog.value = true
                    }
                },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_transcript_or_cert")
            ) {
                Icon(if (selectedTab == 0) Icons.Filled.PostAdd else Icons.Filled.WorkspacePremium, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedTab == 0) "Add Transcript" else "Add Certificate", fontWeight = FontWeight.Bold)
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    // Transcripts Tab
                    item {
                        TranscriptSummaryCard(analytics = analytics, student = student)
                    }

                    item {
                        GradeDistributionChart(transcripts = transcripts, modifier = Modifier.fillMaxWidth())
                    }

                    item {
                        Text(
                            text = "Course Grade Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (transcripts.isEmpty()) {
                        item {
                            EmptyStateCard(message = "No transcripts recorded. Tap '+ Add Transcript' below.")
                        }
                    } else {
                        items(transcripts) { item ->
                            DetailedTranscriptCard(
                                transcript = item,
                                onShare = {
                                    val shareText = "🎓 Course Transcript:\nSubject: ${item.subject}\nGrade: ${item.letterGrade} (${item.gradePoint})\nMarks: ${item.marks}%\nCredits: ${item.credits}\nRemarks: ${item.remarks}\nStudent: ${student?.name} (ID: ${student?.studentId})"
                                    viewModel.shareTargetText.value = shareText
                                }
                            )
                        }
                    }
                } else {
                    // Certificates Tab
                    item {
                        CertificateSummaryHeader(certificatesCount = certificates.size, student = student)
                    }

                    if (certificates.isEmpty()) {
                        item {
                            EmptyStateCard(message = "No extracurricular certificates recorded. Tap '+ Add Certificate' below.")
                        }
                    } else {
                        items(certificates) { cert ->
                            DetailedCertificateCard(
                                certificate = cert,
                                onShare = {
                                    val shareText = "🏆 Extracurricular Award:\n${cert.title}\nIssued by: ${cert.issuer}\nCategory: ${cert.category} • ${cert.issueDate}\nCredential ID: ${cert.verificationId}\nVerified on SCORNER Portal for ${student?.name}"
                                    viewModel.shareTargetText.value = shareText
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
fun TranscriptSummaryCard(analytics: AnalyticsData, student: StudentEntity?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ACADEMIC PERFORMANCE SUMMARY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GPA ${String.format("%.2f", analytics.calculatedGpa)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Cumulative Grade Point Average",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldYellow
                ) {
                    Text(
                        text = "Distinction Honors",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate900,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Credits: ${analytics.totalCredits}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text("Board SSC: ${student?.sscGpa ?: 5.00}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text("Attendance: ${student?.attendanceRate ?: 96}%", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DetailedTranscriptCard(
    transcript: TranscriptEntity,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transcript.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${transcript.semester} • ${transcript.examDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue
                ) {
                    Text(
                        text = "${transcript.letterGrade} (${transcript.gradePoint})",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Marks Scored: ${transcript.marks}/100",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Credits: ${transcript.credits}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (transcript.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Note: ${transcript.remarks}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onShare) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Subject Result")
                }
            }
        }
    }
}

@Composable
fun CertificateSummaryHeader(certificatesCount: Int, student: StudentEntity?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryTeal),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CO-CURRICULAR & OLYMPIAD PROFILE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$certificatesCount Verified Credentials",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "National Olympiads, Tech, Debate & Leadership",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedCertificateCard(
    certificate: CertificateEntity,
    onShare: () -> Unit
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TertiaryAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = certificate.category,
                            color = TertiaryAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = certificate.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Issued by ${certificate.issuer} • ${certificate.issueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate200.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verified Credential", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = certificate.verificationId,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
